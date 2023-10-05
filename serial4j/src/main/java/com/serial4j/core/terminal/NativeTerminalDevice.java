/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, Scrappers Team, The AVR-Sandbox Project, Serial4j API.
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
package com.serial4j.core.terminal;

import com.serial4j.core.serial.SerialPort;
import com.serial4j.core.terminal.control.TerminalControlFlag;
import com.serial4j.core.terminal.control.TerminalInputFlag;
import com.serial4j.core.terminal.control.TerminalLocalFlag;
import com.serial4j.core.terminal.control.TerminalOutputFlag;
import com.serial4j.util.loader.NativeImageLoader;

/**
 * Represents the native Java binding for the Serial-4j API, represented by
 * `com_serial4j_core_terminal_NativeTerminalDevice.h` natively.
 * 
 * @author pavl_g.
 */
@SuppressWarnings("all")
public final class NativeTerminalDevice {

    /*
     * Static initializer: Loads the native image when this object is created or referenced.
     */
    static {
        NativeImageLoader.loadSerial4jNatives();
    }

    private SerialPort serialPort;
    private String[] serialPorts;
    private String readBuffer;

    /**
     * Creates a native terminal device after
     * loading the native library (see the static-initializer).
     *
     * @see TerminalDevice
     */
    NativeTerminalDevice() {
    }

    /**
     * Retrieves the serial port associated with this terminal device.
     *
     * @return the serial port object associated with this terminal device.
     */
    public SerialPort getSerialPort() {
        return this.serialPort;
    }

    /**
     * Retrieves all the available teletype terminal (tty) devices from the filesystem "/dev".
     *
     * @return the available tty devices in an array of strings format.
     */
    public String[] getSerialPorts() {
        return this.serialPorts;
    }

    /**
     * Retrieves the read buffer in a string format after
     * dispatching {@link NativeTerminalDevice#read()}.
     *
     * <p>
     * The read buffer is a native const char*, settled natively by reading data
     * frames using {@link NativeTerminalDevice#read()}.
     * </p>
     *
     * @return the buffer in string format
     */
    public String getReadBuffer() {
        return this.readBuffer;
    }

    /**
     * Initializes the serial port path of the native terminal device before opening the terminal device port.
     * 
     * @param serialPort the object to initialize the serial port with.
     */
    void setSerialPort(final SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    /**
     * Sets up the native environment for this terminal device.
     * 
     * @return (-1) if the jni env pointer is NULL, (1) for successful initialization.
     */
    @Deprecated
    static native int setupJniEnvironment();

    /**
     * Adjusts the native terminal control [c_cflag] of the [termios] structure variable for this terminal device.
     *
     * <p>
     * The terminal control flag controls the way the terminal device r/w the charachters on the console.
     * For more, refer to the POSIX terminal control guide.
     * </p>
     *
     * <p>
     * Default value = tty->c_cflag |= (CREAD | CS8 | CLOCAL); defined by {@link NativeTerminalDevice#initTerminal()}.
     * </p>
     * 
     * @param flag the flag to set the [c_cflag] to.
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     * @see TerminalControlFlag
     */
    native int setTerminalControlFlag(final int flag);

    /**
     * Adjusts the native terminal local [c_lflag] of the [termios] structure variable for this terminal device.
     *
     * <p>
     * The terminal local flag controls the way the terminal device interprets and displays the charachters on the console (i.e local console).
     * For more, refer to the POSIX terminal control guide.
     * </p>
     *
     * <p>
     * Default value = tty->c_lflag &= ~(ICANON | ECHO | ECHOE | ECHOK | ECHOKE | ECHONL | ECHOPRT | ECHOCTL | ISIG | IEXTEN); 
     * defined by {@link NativeTerminalDevice#initTerminal()}.
     * </p>
     *
     * @param flag the flag to set the [c_lflag] to.
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     * @see TerminalLocalFlag
     */    
    native int setTerminalLocalFlag(final int flag);

    /**
     * Adjusts the native terminal input [c_iflag] of the [termios] structure variable for this terminal device.
     *
     * <p>
     * The terminal input flag controls the way the terminal device receives and interprets the charachters on input.
     * For more, refer to the POSIX terminal control guide.
     * </p>
     *
     * <p>
     * Default value = tty->c_iflag = 0x00; defined by {@link NativeTerminalDevice#initTerminal()}.
     * </p>
     *
     * @param flag the flag to set the [c_iflag] to.
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     * @see TerminalInputFlag
     */    
    native int setTerminalInputFlag(final int flag);

    /**
     * Adjusts the native terminal output [c_oflag] of the [termios] structure variable for this terminal device.
     *
     * <p>
     * The terminal output flag controls the way the terminal device transmits and interprets the charachters on output.
     * For more, refer to the POSIX terminal control guide.
     * </p>
     *
     * <p>
     * Default value = tty->c_oflag &= ~(OPOST | ONLCR); defined by {@link NativeTerminalDevice#initTerminal()}.
     * </p>
     *
     * @param flag the flag to set the [c_oflag] to.
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     * @see TerminalOutputFlag
     */    
    native int setTerminalOutputFlag(final int flag);

    /**
     * Retrieves the terminal control flag from this terminal device port descriptor in 64-bit format.
     * 
     * @return the [c_cflag] value in integers.
     */
    native int getTerminalControlFlag();

    /**
     * Retrieves the terminal local flag from this terminal device port descriptor in 64-bit format.
     *
     * @return the [c_lflag] value in integers.
     */
    native int getTerminalLocalFlag();

    /**
     * Retrieves the terminal input flag from this terminal device port descriptor in 64-bit format.
     *
     * @return the [c_iflag] value in integers.
     */
    native int getTerminalInputFlag();

    /**
     * Retrieves the terminal output flag from this terminal device port descriptor in 64-bit format.
     *
     * @return the [c_oflag] value in integerss.
     */
    native int getTerminalOutputFlag();

    /**
     * Adjusts the read mode for this terminal device, the read mode is defined by the read timeout value and the minimum
     * number of bytes to read at that time.
     *
     * <p>
     * Default value = BLOCKING_READ_ONE_CHAR {0, 1}; defined by {@link NativeTerminalDevice#initTerminal()}.
     * </p>
     *
     * @param timeout the value of the read timeout, applied only when the first index of [mode] is 1 (aka read timeout is activated).
     * @param bytes the value of the minimum byte to read in this time.
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     * @see ReadConfiguration
     */
    native int setReadConfigurationMode(final short timeout, final short bytes);

    /**
     * Gets the read configuration for this terminal device defining the timeout value as the first index and
     * the minimum number of read byte in this timeout as the second index.
     *
     * <p>
     * Default value = BLOCKING_READ_ONE_CHAR {0, 1}; defined by {@link NativeTerminalDevice#initTerminal()}.
     * </p>
     *
     * @return an array referring to the read mode, where index [0] represents the read timeout, index [1] represents
     *         the minimum bytes to read.
     */
    native short[] getReadConfigurationMode();

    /**
     * Retrieves the last error encountered by the native code,
     *
     * @return the last error code from the native <errno.h>.
     */
    native int getErrno();

    /**
     * Fetches the available system teletype terminal devices (tty) located within "/dev" directory
     * and insert the result into {@link NativeTerminalDevice#serialPorts}.
     *
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     */
    native int fetchSerialPorts();

    /**
     * Retrieves the baud rate POSIX code for this terminal process, find more at <./usr/include/x86_64-linux-gnu/bits/termios.h>.
     *
     * @return the baud rate code in integers.
     */
    native int getBaudRate();

    /**
     * Writes an integer buffer to this terminal device.
     *
     * @param data an integer data buffer to write up-to 32-bit.
     * @return the number of written bytes in long format.
     */
    native long write(final int data);

    /**
     * Writes a string buffer (const char*) with a length to this terminal device.
     *
     * @param buffer a string buffer to write to this terminal device.
     * @param length the string buffer length in integers, this minimizes the jni native calls.
     * @return the number of written bytes to this terminal device.
     */
    native long write(final String buffer, final int length);

    /**
     * Reads the data from this terminal device and insert the result into the {@link NativeTerminalDevice#readBuffer}
     * string buffer.
     *
     * @return the number of read bytes from this terminal device.
     */
    native long read();

    /**
     * Opens this terminal device using the path to the port [port] in strings and the port permissions [flag] in integers.
     *
     * @param port the port path in strings. 
     * @param flag the flag for the base file control native api [fcntl].
     * @return errno(-1) for failure, errno(1) for success.
     */
    native int openPort(final String port, final int flag);

    /**
     * Initializes this terminal device with the default terminal flags and read timeout properties:
     * <p>
     * <h3> c_cflag: for control mode flags. </h3>
     * Enable these bits:
     * <li> [CREAD]: Allow input to be received. </li>
     * <li> [CS8]: For charachter size 8-bit, you can use the bit mask CSIZE to read this value. </li>
     * <li> [CLOCAL]: Ignore modem status lines (do not check carrier signal). </li>
     * </p>
     *
     * <p>
     * <h3> c_lflag: for local mode flags. </h3>
     * Disable these bits:
     * <li> [ICANON]: Canonical mode (line-by-line) input. </li>
     * <li> [ECHO]: Echo input characters. </li>
     * <li> [ECHOE]: Perform ERASE visually. </li>
     * <li> [ECHOK]: Echo KILL visually. </li>
     * <li> [ECHOKE]: Do not output a newline after echoed KILL. </li>
     * <li> [ECHONL]: Echo NL (in canonical mode) even if echoing is disabled. </li>
     * <li> [ECHOPRT]: Echo deleted characters backward (between \ and / ). </li>
     * <li> [ECHOCTL]: Echo control characters visually (e.g., ^L ). </li>
     * <li> [ISIG]: Enable signal-generating characters (INTR, QUIT, SUSP). </li>
     * <li> [IEXTEN]: Enable extended processing of input characters. </li>
     * </p>
     *
     * <p>
     * <h3> c_oflag: for output mode flags. </h3>
     * Disable these bits:
     * <li> [OPOST]: Perform output postprocessing. </li>
     * <li> [ONLCR]: Map NL to CR-NL on output. </li>
     * </p>
     *
     * <p>
     * <h3> c_iflag: for input mode flags. </h3>
     * Disable all input bit masks.
     * </p>
     *
     * <p>
     * <h3> c_cc: For control characters. </h3>
     * Sets to BLOCKING READ ONE CHAR AT A TIME MODE.
     * </p>
     *
     * <p>
     * Note: This should be called right after {@link NativeTerminalDevice#openPort(String, int)}.
     * </p>
     *
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     */
    native int initTerminal();

    /**
     * Adjusts the baud rate aka. the speed of data transmission in bits/seconds for this bus.
     *
     * @param baudRate the baud rate POSIX native code, find more about baud rate codes at `include/x86_64-linux-gnu/bits/termios.h`
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     */    
    native int setBaudRate(int baudRate);

    /**
     * Closes the serial port of this terminal device releasing the resources.
     *
     * <p>
     * Note: This call clears the {@link NativeTerminalDevice#serialPort#portOpened} to 0,
     * the port descriptor {@link NativeTerminalDevice#serialPort#fd} to 0 and the port path {@link NativeTerminalDevice#serialPort#fd} to "".
     * </p>
     * 
     * @return errno(-1) for failure, errno(-2) for invalid port, errno(1) for success.
     */
    native int closePort();
}