/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk.util;

public final class Pair <N, V> {
    private N first;
    private V second;

    public Pair(N name, V value) {
        this.first = name;
        this.second = value;
    }

    public N getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

}
