package handler;

import annotation.LocalizedRequestMapping;
import condition.LocalizedPatternRequestCondition;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
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
    private final List<Locale> locales;

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    public LocalizedRequestMappingHandlerMapping(MessageSource messageSource, List<Locale> supportedLocales) {
        this.messageSource = messageSource;
        this.locales = supportedLocales;
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
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
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
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> createPatternsRequestCondition(entry.getValue())));
    }

    private Map<Locale, String[]> getLocalizedPatterns(String code) {
        return this.locales.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        locale -> getPatternsForLocale(code, locale)));

    }

    private String[] getPatternsForLocale(String code, Locale locale) {
        return new String[]{messageSource.getMessage(code, null, locale)};
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
