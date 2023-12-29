package se.ead.base.savings.encryption;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncryptedListenerConfiguration {

	EntityManagerFactory entityManagerFactory;
	EncryptedListener encryptedListener;

	@PostConstruct
	private void onApplicationStart() {

		log.info("Registering the encryption listener on application start event");

		SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
		EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

		registry.getEventListenerGroup(EventType.PRE_LOAD).appendListener(encryptedListener);
		registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(encryptedListener);
		registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(encryptedListener);

	}
}