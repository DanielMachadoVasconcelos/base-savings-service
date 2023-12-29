package se.ead.base.savings.encryption;

import java.nio.charset.StandardCharsets;
import org.mapstruct.Mapper;

@Mapper
public interface StringToBytesMapper {

	static byte[] encode(String value) {
		return value != null ? value.getBytes(StandardCharsets.UTF_8) : null;
	}

	static String decode(byte[] value) {
		return value != null ? new String(value, StandardCharsets.UTF_8) : null;
	}
}
