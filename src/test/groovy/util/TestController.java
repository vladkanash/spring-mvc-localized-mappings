package util;

import request.annotation.LocalizedRequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class TestController {

    @LocalizedRequestMapping(value = "test", method = RequestMethod.GET)
    public void simpleMapping1() {}

}
