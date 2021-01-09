package huh.enterprises.dracarys.chain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventQuery {

	Long id;
	String workflow;
	int limit;

}
