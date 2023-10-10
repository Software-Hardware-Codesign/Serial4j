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
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.serial4j.example.exception;

import com.serial4j.core.serial.SerialPort;
import com.serial4j.core.serial.throwable.TooManyOpenedFilesException;
import com.serial4j.core.terminal.Permissions;
import com.serial4j.core.terminal.TerminalDevice;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Isolates and examines the {@link TooManyOpenedFilesException}
 * by opening files in an infinite loop, the loop is terminated
 * when this exception is thrown, and the application exits with
 * the native error code {@link com.serial4j.core.errno.Errno#EMFILE}.
 *
 * @author pavl_g
 */
public final class TestTooManyOpenedFilesException {
    public static void main(String[] args) {
        final TerminalDevice ttyDevice = new TerminalDevice();
        final Permissions permissions = Permissions.createEmptyPermissions().append(
                Permissions.Const.O_RDWR,
                Permissions.Const.O_NOCTTY
        );
        ttyDevice.setPermissions(permissions);
        for (; ; ) {
            try {
                ttyDevice.openPort(new SerialPort(args[0]));
            } catch (TooManyOpenedFilesException e) {
                Logger.getLogger(TestTooManyOpenedFilesException.class.getName())
                        .log(Level.SEVERE, e.getMessage() + " " + ttyDevice.getSerialPort().getPath(), e);
                System.exit(e.getCausingErrno().getValue());
            }
        }
    }
}