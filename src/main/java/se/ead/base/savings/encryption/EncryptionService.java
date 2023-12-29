package se.ead.base.savings.encryption;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.cryptography.materialproviders.IKeyring;
import software.amazon.cryptography.materialproviders.model.OnDecryptInput;
import software.amazon.cryptography.materialproviders.model.OnDecryptOutput;
import software.amazon.cryptography.materialproviders.model.OnEncryptInput;
import software.amazon.cryptography.materialproviders.model.OnEncryptOutput;

@Slf4j
@Component
@AllArgsConstructor
public class EncryptionService {

	AwsCrypto crypto;
	CryptoMaterialsManager cryptoMaterialsManager;

	public byte[] encrypt(byte[] valueToEncrypt) {
		return crypto.encryptData(cryptoMaterialsManager, valueToEncrypt).getResult();
	}

	public byte[] decrypt(byte[] valueToDecrypt) {
		return crypto.decryptData(cryptoMaterialsManager, valueToDecrypt).getResult();
	}
}
