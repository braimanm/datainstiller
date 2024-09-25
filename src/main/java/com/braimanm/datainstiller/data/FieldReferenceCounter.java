/*
Copyright 2010-2024 Michael Braiman braimanm@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.braimanm.datainstiller.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class FieldReferenceCounter {
    private final Map<String, Integer> fieldClasses;

    public FieldReferenceCounter() {
        fieldClasses = new HashMap<>();
    }

    private String getKey(Field field) {
        return field.getDeclaringClass().getCanonicalName() + "." + field.getName();
    }

    public Integer getCounter(Field field) {
        String key = getKey(field);
        if (fieldClasses.containsKey(key)) {
            return fieldClasses.get(key);
        } else {
            fieldClasses.put(key, 0);
            return 0;
        }
    }

    public void incrementCounter(Field field) {
        int i = getCounter(field) + 1;
        fieldClasses.put(getKey(field), i);
    }

    public void reset(Field field) {
        fieldClasses.put(getKey(field), 0);
    }
}
