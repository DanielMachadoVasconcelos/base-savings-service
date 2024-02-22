package se.ead.base.savings.encryption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@AllArgsConstructor
@NoArgsConstructor
public class EncryptionProperties {

	@Value("${application.encryption.aws_kms_cmk:#{'arn:aws:kms:eu-west-1:000000000000:alias/test-only'}}")
	String awsKmsCmk;

	@Value(value = "${application.encryption.aws-kms-local-port:#{4566}}")
	Integer localPort;
}
