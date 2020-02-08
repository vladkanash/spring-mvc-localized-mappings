package request.handler;

import request.annotation.LocalizedRequestMapping;
import request.condition.LocalizedPatternRequestCondition;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalizedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final MessageSource messageSource;

    private final List<Locale> supportedLocales;

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    public LocalizedRequestMappingHandlerMapping(MessageSource messageSource, List<Locale> supportedLocales) {
        this.messageSource = messageSource;
        this.supportedLocales = supportedLocales;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();

        this.config = new RequestMappingInfo.BuilderConfiguration();
        this.config.setUrlPathHelper(getUrlPathHelper());
        this.config.setPathMatcher(getPathMatcher());
        this.config.setSuffixPatternMatch(true);
        this.config.setTrailingSlashMatch(true);
        this.config.setRegisteredSuffixPatternMatch(false);
        this.config.setContentNegotiationManager(getContentNegotiationManager());
    }

    @Override
    protected AbstractRequestCondition<?> getCustomMethodCondition(Method method) {
        LocalizedRequestMapping localizedMapping = AnnotatedElementUtils.findMergedAnnotation(method, LocalizedRequestMapping.class);
        if (localizedMapping != null) {
            var mappingsMap = getPatternConditionMap(localizedMapping);
            return new LocalizedPatternRequestCondition(mappingsMap);
        }
        return null;
    }

    private Map<Locale, PatternsRequestCondition> getPatternConditionMap(LocalizedRequestMapping localizedMapping) {
        return getLocalizedPatterns(localizedMapping.code())
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().length > 0)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createPatternsRequestCondition(entry.getValue())));
    }

    private Map<Locale, String[]> getLocalizedPatterns(String code) {
        return this.supportedLocales.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        locale -> getPatternsForLocale(code, locale)));

    }

    private String[] getPatternsForLocale(String code, Locale locale) {
        try {
            return new String[]{messageSource.getMessage(code, null, locale)};
        } catch (NoSuchMessageException nsme) {
            return new String[0];
        }
    }

    private PatternsRequestCondition createPatternsRequestCondition(String[] paths) {
        return new PatternsRequestCondition(paths,
                config.getUrlPathHelper(),
                config.getPathMatcher(),
                config.useSuffixPatternMatch(),
                config.useTrailingSlashMatch(),
                config.getFileExtensions());
    }
}
