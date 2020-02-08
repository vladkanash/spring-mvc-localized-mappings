package request.handler

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import util.TestController

import java.lang.reflect.Method

@SpringJUnitWebConfig(locations = "classpath:applicationContext.xml")
class LocalizedRequestMappingHandlerMappingTest {

    @Autowired
    LocalizedRequestMappingHandlerMapping testedInstance

    @Test
    void shouldCreateLocalizedRequestCondition() {
        def method = getTestMethod("simpleMapping1")
        def actual = testedInstance.getCustomMethodCondition(method)

        assert actual?.content?.size() == 2
    }

    @Test
    void shouldCreateEmptyConditionIfNo() {
        def method = getTestMethod("simpleMapping1")
        def actual = testedInstance.getCustomMethodCondition(method)

        assert actual?.content?.size() == 2
    }

    private static Method getTestMethod(String name) {
        return TestController.class.getMethod(name, null)
    }
}
