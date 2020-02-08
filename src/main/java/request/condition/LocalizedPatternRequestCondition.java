package request.condition;

import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class LocalizedPatternRequestCondition extends AbstractRequestCondition<PatternsRequestCondition> {

    private final Map<Locale, PatternsRequestCondition> requestConditionMap;

    public LocalizedPatternRequestCondition(Map<Locale, PatternsRequestCondition> requestConditionMap) {
        this.requestConditionMap = requestConditionMap;
    }

    private Optional<PatternsRequestCondition> getLocalePatternCondition(HttpServletRequest request) {
        return Optional.ofNullable(request)
                .map(HttpServletRequest::getLocale)
                .map(requestConditionMap::get);
    }

    @Override
    public PatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        return getLocalePatternCondition(request)
                .map(condition -> condition.getMatchingCondition(request))
                .orElse(null);
    }

    @Override
    protected Collection<?> getContent() {
        return requestConditionMap.values();
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    @Override
    public PatternsRequestCondition combine(PatternsRequestCondition other) {
        //TODO combine multiple localized conditions
        return null;
    }

    @Override
    public int compareTo(PatternsRequestCondition other, HttpServletRequest request) {
        return Optional.ofNullable(getMatchingCondition(request))
                .map(condition -> other.compareTo(condition, request))
                .orElse(1);
    }
}
