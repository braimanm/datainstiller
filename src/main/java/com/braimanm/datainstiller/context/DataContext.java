package com.braimanm.datainstiller.context;

import com.braimanm.datainstiller.data.GlobalAliases;

public class DataContext {
    private static final ThreadLocal<GlobalAliases> globalAliases = ThreadLocal.withInitial(GlobalAliases::new);

    public static GlobalAliases getGlobalAliases() {
        return globalAliases.get();
    }

}
