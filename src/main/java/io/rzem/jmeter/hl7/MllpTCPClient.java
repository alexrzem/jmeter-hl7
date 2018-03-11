package io.rzem.jmeter.hl7;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.protocol.tcp.sampler.AbstractTCPClient;
import org.apache.jmeter.protocol.tcp.sampler.ReadException;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MllpTCPClient extends AbstractTCPClient {

	private static final Logger log = LoggerFactory.getLogger(MllpTCPClient.class);

	private static final byte MLLP_START = 0x0b;
	private static final byte MLLP_END1 = 0x1c;
	private static final byte MLLP_END2 = 0x0d;

	private static final String NL = "\n";
	private static final String CR = "\r";

	private static final String EOLBYTE_KEY = "tcp.eolByte";
	private static final int EOLBYTE_DEFAULT = 13;

	public MllpTCPClient() {
		super();

		setEolByte(JMeterUtils.getPropDefault(EOLBYTE_KEY, EOLBYTE_DEFAULT));
	}

	public void write(OutputStream output, InputStream input) throws IOException {
		StringWriter writer = new StringWriter();

		IOUtils.copy(input, writer, UTF_8);

		write(output, writer.toString());
	}

	/**
	 *
	 * @param os
	 *            -
	 *            OutputStream for socket
	 * @param s
	 *            -
	 *            String to write
	 * @throws IOException
	 *             when writing fails
	 */
	public void write(OutputStream output, String input) throws IOException {
		try {
			output.write(MLLP_START);
			output.write(StringUtils.replace(input, NL, CR).getBytes(UTF_8));
			output.write(MLLP_END1);
			output.write(MLLP_END2);
			output.flush();
		} catch (Throwable e) {
			log.warn("{} occured while writing to stream: {}", new Object[] { e.getClass().getSimpleName(), e.getMessage() });
		}
	}

	public String read(InputStream input) throws ReadException {
		StringWriter output = new StringWriter();

		try {
			IOUtils.copy(input, output, UTF_8);
		} catch (Throwable e) {
			log.warn("{} occured while reading from stream: {}", new Object[] { e.getClass().getSimpleName(), e.getMessage() });
		}

		return output.toString();
	}

}
