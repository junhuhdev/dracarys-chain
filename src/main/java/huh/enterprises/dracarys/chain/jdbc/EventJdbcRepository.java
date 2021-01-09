package huh.enterprises.dracarys.chain.jdbc;

import huh.enterprises.dracarys.chain.event.XEvent;
import huh.enterprises.dracarys.chain.event.XEventQuery;
import huh.enterprises.dracarys.chain.event.XEventSQLQuery;
import huh.enterprises.dracarys.chain.event.XEventState;
import huh.enterprises.dracarys.chain.event.XEventTransaction;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

public class EventJdbcRepository extends BaseJdbcRepository<XEventQuery, XEventTransaction> {

	private final Tx tx;

	public EventJdbcRepository(DataSource dataSource, PlatformTransactionManager txManager) {
		super(dataSource, 50);
		this.tx = new Tx(new TransactionTemplate(txManager));
	}

	private static SqlParameterSource getParams(XEventTransaction record) {
		String roots = record.toXml();
		return new MapSqlParameterSource()
				.addValue("id", record.getId())
				.addValue("parentId", record.getParentId())
				.addValue("state", record.getState().name())
				.addValue("workflow", record.getWorkflow())
				.addValue("history", new SqlLobValue(roots, new DefaultLobHandler()), Types.CLOB);
	}

	public XEventTransaction findById(Long id) {
		return select(XEventQuery.builder()
				.id(id)
				.build());
	}

	public XEventTransaction findByReferenceId(String referenceId) {
		var params = new MapSqlParameterSource()
				.addValue("referenceId", referenceId);
		var response = getNamedParameterJdbcTemplate()
				.query("SELECT * FROM XEVENT_TRANSACTION WHERE REFERENCE_ID = :referenceId",
						params,
						new RowExtractor());
		return Optional.ofNullable(response).orElseThrow(() -> new IllegalStateException(String.format("Event %s does not exist", referenceId)));
	}

	public XEventTransaction select(XEventQuery query) {
		return getNamedParameterJdbcTemplate()
				.query("SELECT * FROM XEVENT_TRANSACTION WHERE id = :id",
						new BeanPropertySqlParameterSource(query),
						new RowExtractor());
	}

	public List<XEventTransaction> query(XEventSQLQuery query) {
		return getNamedParameterJdbcTemplate()
				.query(query.getSql(),
						query.getParams(),
						new ListExtractor());
	}

	public List<XEventTransaction> selectAllUnprocessedByWorkflow(XEventQuery query) {
		return getNamedParameterJdbcTemplate()
				.query("SELECT * FROM XEVENT_TRANSACTION WHERE STATE = 'REGISTERED' AND WORKFLOW = :workflow ORDER BY CREATED ASC FETCH FIRST :limit ROWS ONLY ",
						new BeanPropertySqlParameterSource(query),
						new ListExtractor());
	}

	public List<XEventTransaction> selectAllUnprocessed(XEventQuery query) {
		return getNamedParameterJdbcTemplate()
				.query("SELECT * FROM XEVENT_TRANSACTION WHERE STATE = 'REGISTERED' ORDER BY CREATED ASC FETCH FIRST :limit ROWS ONLY ",
						new BeanPropertySqlParameterSource(query),
						new ListExtractor());
	}

	public boolean batchInsert(List<XEventTransaction> records) {
		var params = records.stream()
				.map(EventJdbcRepository::getParams)
				.toArray(SqlParameterSource[]::new);
		var count = tx.wrap(() -> getNamedParameterJdbcTemplate()
				.batchUpdate("INSERT INTO XEVENT_TRANSACTION (PARENT_ID, STATE, WORKFLOW, HISTORY) VALUES (:parentId, :state, :workflow, :history)", params));
		return Arrays.stream(count).count() == records.size();
	}

	public boolean insert(XEventTransaction record) {
		SqlParameterSource params = getParams(record);
		var count = tx.wrap(() -> getNamedParameterJdbcTemplate()
				.update("INSERT INTO XEVENT_TRANSACTION (PARENT_ID, STATE, WORKFLOW, HISTORY) VALUES (:parentId, :state, :workflow, :history)",
						params));
		return count == 1;
	}

	public boolean update(XEventTransaction record) {
		SqlParameterSource params = getParams(record);
		var count = tx.wrap(() -> getNamedParameterJdbcTemplate()
				.update("UPDATE XEVENT_TRANSACTION SET HISTORY = :history, STATE = :state, WORKFLOW = :workflow WHERE ID = :id",
						params));
		return count == 1;
	}

	public boolean lock(XEventTransaction record) {
		SqlParameterSource params = getParams(record);
		var count = tx.wrap(() -> getNamedParameterJdbcTemplate()
				.update("UPDATE XEVENT_TRANSACTION SET STATE = :state WHERE ID = :id AND STATE <> :state",
						params));
		return count == 1;
	}

	private static final class RowExtractor extends BaseExtractor implements ResultSetExtractor<XEventTransaction> {

		@Override
		public XEventTransaction extractData(ResultSet rs) throws SQLException, DataAccessException {
			XEventTransaction record = null;
			if (!rs.next()) {
				return null;
			} else {
				do {
					if (isNull(record)) {
						record = map(rs);
					}
				} while (rs.next());
			}
			return record;
		}

	}

	private static final class ListExtractor extends BaseExtractor implements ResultSetExtractor<List<XEventTransaction>> {

		@Override
		public List<XEventTransaction> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Map<Long, XEventTransaction> records = new HashMap<>();
			if (!rs.next()) {
				return Collections.emptyList();
			} else {
				do {
					Long id = rs.getLong("ID");
					var record = records.get(id);
					if (isNull(record)) {
						record = map(rs);
						records.put(id, record);
					}
				} while (rs.next());
			}
			return new ArrayList<>(records.values());
		}

	}

	private static class BaseExtractor {

		public XEventTransaction map(ResultSet rs) throws SQLException {
			String xml = rs.getString("HISTORY");
			List<XEvent> events = XEventTransaction.fromXml(xml);
			return XEventTransaction.builder()
					.id(rs.getLong("ID"))
					.parentId(rs.getLong("PARENT_ID"))
					.referenceId(rs.getString("REFERENCE_ID"))
					.state(XEventState.valueOf(rs.getString("STATE")))
					.workflow(rs.getString("WORKFLOW"))
					.events(events)
					.build();
		}

	}

}
