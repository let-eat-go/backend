package com.leteatgo.global.lock.util;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {

    private CustomSpringELParser() {
    }

    /**
     * Spring EL을 사용하여 동적으로 값을 가져온다.
     *
     * @param parameterNames 파라미터 이름
     * @param args           파라미터 값
     * @param key            SpEL 표현식
     * @return SpEL 표현식에 따른 값
     */
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(key).getValue(context, Object.class);
    }

}
