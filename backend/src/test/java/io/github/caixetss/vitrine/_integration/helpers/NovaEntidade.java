package io.github.caixetss.vitrine._integration.helpers;

import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

final class NovaEntidade {

	private NovaEntidade() {
	}

	static <T> T instancia(Class<T> tipo) {
		T entidade = BeanUtils.instantiateClass(tipo);
		Instant agora = Instant.now();
		ReflectionTestUtils.setField(entidade, "criadoEm", agora);
		ReflectionTestUtils.setField(entidade, "atualizadoEm", agora);
		return entidade;
	}

}
