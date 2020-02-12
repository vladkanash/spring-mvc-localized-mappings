package request.handler;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import request.annotation.LocalizedRequestMapping;
import request.condition.LocalizedPatternRequestCondition;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static java.util.stream.Collectors.toMap;

public class LocalizedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private final Properties mappings;
    private final String basename;

    private RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    public LocalizedRequestMappingHandlerMapping(Properties properties, String basename) {
        this.mappings = properties;
        this.basename = basename;
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
        return loadLocalizedPatterns(localizedMapping.code())
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().length > 0)
                .collect(toMap(
                        Map.Entry::getKey,
                        entry -> createPatternsRequestCondition(entry.getValue())));
    }

    private Map<Locale, String[]> loadLocalizedPatterns(String code) {
        var mappingBaseName = this.basename + "." + code;
        return mappings.stringPropertyNames()
                .stream()
                .filter(key -> key.startsWith(mappingBaseName))
                .collect(toMap(
                        key -> Locale.forLanguageTag(key.substring(mappingBaseName.length() + 1)),
                        key -> new String[]{mappings.getProperty(key)}));
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
