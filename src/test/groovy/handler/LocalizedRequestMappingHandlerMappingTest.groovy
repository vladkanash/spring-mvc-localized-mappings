package handler

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig

import java.lang.reflect.Method

@SpringJUnitWebConfig(locations = "classpath:applicationContext.xml")
class LocalizedRequestMappingHandlerMappingTest {

    private static final String CODE = 'homepage'

    @Autowired
    LocalizedRequestMappingHandlerMapping testedInstance

    @Mock
    Method method;

    @Test
    void shouldLoadMappings() {
        def result = testedInstance.getCustomMethodCondition(method)
        assert result != null
    }
}
