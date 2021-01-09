package huh.enterprises.dracarys.chain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class XEventQuery {

	Long id;
	String workflow;
	int limit;

}
