/* Copyright 2009 Jeremy Chone - Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.snowfk;

import java.util.Map;

import org.snowfk.util.MapUtil;

public class SnowRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 6761082312332003153L;

    private Enum              causeEnum;

    private Object[]          nameAndValueArray;

    protected Throwable       usefulCause;

    public SnowRuntimeException(Throwable cause) {
        super(cause);
    }

    public SnowRuntimeException(Enum causeEnum, Throwable cause, Object... nameAndValueArray) {
        super(causeEnum.name(), cause);
        this.causeEnum = causeEnum;
        this.nameAndValueArray = nameAndValueArray;
    }

    public SnowRuntimeException(Enum causeEnum, Object... nameAndValueArray) {
        super(causeEnum.name());
        this.causeEnum = causeEnum;
        this.nameAndValueArray = nameAndValueArray;
    }

    @Override
    public String getMessage() {

        if (causeEnum != null) {
            StringBuilder sb = null;

            sb = new StringBuilder(causeEnum.name());
            if (nameAndValueArray != null) {
                sb.append("  {");
                Map m = MapUtil.mapIt(nameAndValueArray);
                boolean first = true;
                for (Object name : m.keySet()) {
                    if (!first) {
                        sb.append(',');
                    } else {
                        first = false;
                    }

                    sb.append('"').append(name).append('"');
                    sb.append(':');
                    sb.append('"').append(m.get(name)).append('"');
                }
                sb.append('}');
            }
            return sb.toString();
        } else if (usefulCause != null){
            return usefulCause.getMessage();
        }else {
            return super.getMessage();
        }
    }

}
