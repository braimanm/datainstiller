package com.braimanm.datainstiller.data;

import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.introspection.JexlPermissions;

public class GlobalAliases extends DataAliases {
    private final JexlContext jexlContext = new MapContext(this);
    private final JxltEngine jxltEngine = new JexlBuilder().permissions(JexlPermissions.UNRESTRICTED)
            .strict(true).silent(false).create().createJxltEngine();

    public Object evaluateExpression(String expression) {
        JxltEngine.Expression expr = jxltEngine.createExpression(expression);
        return expr.evaluate(jexlContext);
    }

}
