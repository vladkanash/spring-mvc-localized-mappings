package controller

import config.WebConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringJUnitWebConfig(classes = WebConfig.class)
class HomepageControllerTest {

    private MockMvc mockMvc

    @BeforeEach
    void setup(WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void shouldProcessSimpleUSRequest() {
        mockMvc.perform(get('/homepage')
                .locale(Locale.US))
                .andExpect(status().isOk())
    }

    @Test
    void shouldProcessInvalidUSRequest() {
        mockMvc.perform(get('/hogar')
                .locale(Locale.US))
                .andExpect(status().isNotFound())
    }

    @Test
    void shouldProcessSimpleFRRequest() {
        mockMvc.perform(get('/hogar')
                .locale(Locale.FRANCE))
                .andExpect(status().isOk())
    }

    @Test
    void shouldProcessRootRequest() {
        mockMvc.perform(get('/')
                .locale(Locale.FRANCE))
                .andExpect(status().isNotFound())
    }
}
