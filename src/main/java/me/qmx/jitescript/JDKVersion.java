/**
 *  Copyright 2012 Douglas Campos <qmx@qmx.me>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.qmx.jitescript;

import org.objectweb.asm.Opcodes;

public enum JDKVersion implements Opcodes {
    V1_6(Opcodes.V1_6),
    V1_7(Opcodes.V1_7);

    private final int ver;

    JDKVersion(int ver) {
        this.ver = ver;
    }

    public int getVer() {
        return ver;
    }
}
