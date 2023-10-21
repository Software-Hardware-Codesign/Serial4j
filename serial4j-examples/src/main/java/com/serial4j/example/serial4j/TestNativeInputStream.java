/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, Scrappers Team, The Arithmos Project.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS
 */

package com.serial4j.example.serial4j;

import com.serial4j.core.serial.SerialPort;
import com.serial4j.core.terminal.FilePermissions;
import com.serial4j.core.terminal.NativeBufferInputStream;
import com.serial4j.core.terminal.TerminalDevice;

/**
 * Isolates and tests the {@link NativeBufferInputStream} API.
 *
 * @author pavl_g
 */
public final class TestNativeInputStream {
    public static void main(String[] args) {
        final TerminalDevice ttyDevice = new TerminalDevice();
        final FilePermissions filePermissions = (FilePermissions) FilePermissions.build()
                        .append(FilePermissions.OperativeConst.O_CREATE, FilePermissions.OperativeConst.O_RDWR);
        ttyDevice.setOperativeFilePermissions(filePermissions);
        ttyDevice.openPort(new SerialPort(args[0]));
        try (final NativeBufferInputStream inputStream = new NativeBufferInputStream(ttyDevice)) {
            final StringBuffer buffer = new StringBuffer();
            while (inputStream.read() > 0) {
                buffer.append(inputStream.getBuffer());
                if (buffer.toString().contains("\n\r")) {
                    break;
                }
            }
            System.out.println(buffer);
        }
    }
}
