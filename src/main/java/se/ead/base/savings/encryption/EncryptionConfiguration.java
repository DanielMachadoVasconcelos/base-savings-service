package se.ead.base.savings.encryption;

import static com.amazonaws.client.builder.AwsClientBuilder.*;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager;
import com.amazonaws.encryptionsdk.caching.CryptoMaterialsCache;
import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSAsyncClientBuilder;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class EncryptionConfiguration {

	Environment environment;

	@PostConstruct
	private void setupFakeAwsCredentials() {
		if (Collections.disjoint(List.of(environment.getActiveProfiles()), List.of("local", "integration-test"))) {
			return;
		}

		// This is mostly needed since the encryptor library won't allow us to override the default
		// credentials lookup-chain in the AWS SDK. This is the only way to require no manual setup
		// like setting AWS environment-variables or creating an AWS-credentials file.
		System.setProperty("aws.accessKeyId", "anything");
		System.setProperty("aws.secretKey", "anything");
	}

	@Bean
	public CommitmentPolicy commitmentPolicy() {
		return  CommitmentPolicy.RequireEncryptRequireDecrypt;
	}

	@Bean
	public AwsCrypto crypto(CommitmentPolicy commitmentPolicy) {
		return AwsCrypto.builder().withCommitmentPolicy(commitmentPolicy).build();
	}

	@Bean
	@Profile({"local", "integration-test"})
	public MasterKeyProvider kmsProvider(EncryptionProperties properties) {
		String defaultRegion = Regions.EU_WEST_1.getName();
		return KmsMasterKeyProvider.builder()
				.withDefaultRegion(defaultRegion)
				.withCustomClientFactory(regionName ->
						AWSKMSAsyncClientBuilder.standard()
							.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("fake", "fake")))
							.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:5566", defaultRegion))
							.build()
				)
				.buildStrict(properties.getAwsKmsCmk());
	}

	@Bean
	@Profile({"local", "integration-test"})
	public CryptoMaterialsManager cryptoMaterialsManager(MasterKeyProvider kmsProvider) {
		// Configure the cache size and timeout as needed
		int cacheMaxEntries = 100; // example max entries
		long cacheMaxEntryAgeMillis = 60000; // example max age in milliseconds

		CryptoMaterialsCache cache = new LocalCryptoMaterialsCache(cacheMaxEntries);

		return CachingCryptoMaterialsManager.newBuilder()
				.withMasterKeyProvider(kmsProvider)
				.withCache(cache)
				.withMaxAge(cacheMaxEntryAgeMillis, TimeUnit.MILLISECONDS)
				.build();
	}
}
