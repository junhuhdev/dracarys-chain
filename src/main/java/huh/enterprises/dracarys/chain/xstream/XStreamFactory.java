package huh.enterprises.dracarys.chain.xstream;

import com.thoughtworks.xstream.XStream;

public class XStreamFactory {

	private final static XStream xstream;

	static {
		xstream = new XStream();
	}

	public static XStream xstream() {
		return xstream;
	}

}
