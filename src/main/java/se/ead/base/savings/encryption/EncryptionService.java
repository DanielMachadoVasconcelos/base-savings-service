package se.ead.base.savings.encryption;

import com.amazonaws.SdkClientException;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.ParsedCiphertext;
import com.amazonaws.encryptionsdk.exception.AwsCryptoException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
@AllArgsConstructor
public class EncryptionService {

	AwsCrypto crypto;
	CryptoMaterialsManager cryptoMaterialsManager;

	public byte[] encrypt(byte[] valueToEncrypt, EncryptionContext encryptionContext) {
		try {

			if (encryptionContext == null  || valueToEncrypt == null) {
				throw new IllegalArgumentException("Wrong Encryption Context: The context should not be empty");
			}

			Map<String, String> encryptionContextMap = Map.of(
					"primaryKey", encryptionContext.primaryKey(),
					"column", encryptionContext.column(),
					"table", encryptionContext.table()
			);

			return crypto.encryptData(cryptoMaterialsManager, valueToEncrypt, encryptionContextMap).getResult();
		} catch (AwsCryptoException | IllegalStateException e) {
			throw new IllegalStateException("There was an error encrypting the data", e);
		}
	}

	public byte[] decrypt(byte[] valueToDecrypt, EncryptionContext encryptionContext) {
		try {
			return crypto.decryptData(cryptoMaterialsManager, valueToDecrypt).getResult();
		} catch (SdkClientException | AwsCryptoException | IllegalStateException e) {
			throw new IllegalStateException("There was an error decrypting the data", e);
		}
	}

	public record EncryptionContext(String primaryKey, String column, String table) {
	}
}
