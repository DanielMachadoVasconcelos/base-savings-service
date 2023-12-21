package se.ead.base.savings.encryption;

import static se.ead.base.savings.encryption.EncryptionService.*;

import jakarta.persistence.Column;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncryptedListener implements PreInsertEventListener, PreUpdateEventListener, PreLoadEventListener {

	EncryptionService encryptionService;

	@Override
	public boolean onPreInsert(PreInsertEvent event) {

		Function<CypherInput, byte[]> cypher =
				cypherInput -> encryptionService.encrypt(cypherInput.getValue(), cypherInput.getEncryptionContext());

		replaceFieldValue(cypher, event.getEntity(), event.getPersister(), event.getState(), event.getId());

		return false;
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {

		Function<CypherInput, byte[]> cypher =
				cypherInput -> encryptionService.encrypt(cypherInput.getValue(), cypherInput.getEncryptionContext());

		replaceFieldValue(cypher, event.getEntity(), event.getPersister(), event.getState(), event.getId());

		return false;
	}

	@Override
	public void onPreLoad(PreLoadEvent event) {

		Function<CypherInput, byte[]> cypher =
				cypherInput -> encryptionService.decrypt(cypherInput.getValue(), cypherInput.getEncryptionContext());

		replaceFieldValue(cypher, event.getEntity(), event.getPersister(), event.getState(), event.getId());

	}

	private static void replaceFieldValue(
			Function<CypherInput, byte[]> cypher,
			Object entity,
			EntityPersister entityPersister,
			Object[] state,
			Object id) {

		// This uses the fact that the encrypted and unencrypted field is a byte-array, so we can just apply
		// the cypher method to the entity field

		var fieldsToEncryptOrDecrypt = Arrays.stream(entity.getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(Encrypted.class));

		fieldsToEncryptOrDecrypt.forEach(field -> {

			var encryptionContext = getEncryptionContext(field, id, entityPersister);

			int propertyIndex = Arrays.asList(entityPersister.getPropertyNames()).indexOf(field.getName());

			// Null is allowed here, if the field isn't nullable it'll be caught elsewhere
			if(state[propertyIndex] == null) {
				return;
			}

			if (!(state[propertyIndex] instanceof byte[] fieldValue)) {
				throw new IllegalStateException("Encrypted annotation can only be used for byte-arrays");
			}

			state[propertyIndex] = cypher.apply(new CypherInput(fieldValue, encryptionContext));

		});

	}

	private static EncryptionContext getEncryptionContext(Field field, Object id, EntityPersister entityPersister) {

		// Use the Hibernate-column name in-case the field name changes.
		var columnName = field.getAnnotation(Column.class).name();
		if (columnName == null) {
			throw new IllegalArgumentException("Column name must be specified for encryption to work");
		}

		if (!(entityPersister instanceof AbstractEntityPersister abstractEntityPersister)) {
			throw new IllegalArgumentException("Wrong persister found");
		}

		if (id instanceof UUID primaryKey) {
			return new EncryptionContext(primaryKey.toString(), columnName, abstractEntityPersister.getTableName());
		}

		if (id instanceof String primaryKey) {
			return new EncryptionContext(primaryKey, columnName, abstractEntityPersister.getTableName());
		}

		throw new IllegalArgumentException("Primary key must be String or UUID");
	}


	@Value
	private static class CypherInput {
		byte[] value;
		EncryptionContext encryptionContext;
	}

}
