package se.ead.base.savings.encryption;

import java.util.Arrays;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncryptedListener implements PreInsertEventListener, PreUpdateEventListener, PreLoadEventListener {

	EncryptionService encryptionService;

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		Function<byte[], byte[]> cypher = cypherInput -> encryptionService.encrypt(cypherInput);
		replaceFieldValue(cypher, event.getEntity(), event.getPersister(), event.getState());
		return false;
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		Function<byte[], byte[]> cypher = cypherInput -> encryptionService.encrypt(cypherInput);
		replaceFieldValue(cypher, event.getEntity(), event.getPersister(), event.getState());
		return false;
	}

	@Override
	public void onPreLoad(PreLoadEvent event) {

		Function<byte[], byte[]> cypher = cypherInput -> encryptionService.decrypt(cypherInput);
		replaceFieldValue(cypher, event.getEntity(), event.getPersister(), event.getState());

	}

	private static void replaceFieldValue(Function<byte[], byte[]> cypher,
										  Object entity,
										  EntityPersister entityPersister,
										  Object[] state) {

		// This uses the fact that the encrypted and unencrypted field is a byte-array, so we can just apply
		// the cypher method to the entity field

		var fieldsToEncryptOrDecrypt = Arrays.stream(entity.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(Encrypted.class));

		fieldsToEncryptOrDecrypt.forEach(field -> {

			int propertyIndex = Arrays.asList(entityPersister.getPropertyNames()).indexOf(field.getName());

			// Null is allowed here, if the field isn't nullable it'll be caught elsewhere
			if(state[propertyIndex] == null) {
				return;
			}

			if (!(state[propertyIndex] instanceof byte[] fieldValue)) {
				throw new IllegalStateException("Encrypted annotation can only be used for byte-arrays");
			}

			state[propertyIndex] = cypher.apply(fieldValue);
		});

	}
}
