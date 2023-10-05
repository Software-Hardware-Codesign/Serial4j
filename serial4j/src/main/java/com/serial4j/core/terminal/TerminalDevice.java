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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.serial4j.core.errno.Errno;
import com.serial4j.core.errno.ErrnoToException;
import com.serial4j.core.serial.SerialPort;
import com.serial4j.core.serial.throwable.PermissionDeniedException;
import com.serial4j.core.serial.throwable.BrokenPipeException;
import com.serial4j.core.serial.throwable.NoSuchDeviceException;
import com.serial4j.core.serial.throwable.TooManyOpenedFilesException;
import com.serial4j.core.serial.throwable.ReadOnlyFileSystemException;
import com.serial4j.core.serial.throwable.FileAlreadyOpenedException;
import com.serial4j.core.serial.throwable.BadFileDescriptorException;
import com.serial4j.core.serial.throwable.NotTtyDeviceException;
import com.serial4j.core.serial.throwable.InvalidArgumentException;
import com.serial4j.core.serial.throwable.FileIsDirectoryException;
import com.serial4j.core.serial.throwable.InterruptedSystemCallException;
import com.serial4j.core.serial.throwable.FileTableOverflowException;
import com.serial4j.core.serial.throwable.NoSuchFileException;
import com.serial4j.core.serial.throwable.NoSpaceLeftException;
import com.serial4j.core.serial.throwable.InvalidPortException;
import com.serial4j.core.serial.throwable.NoAvailableTtyDevicesException;
import com.serial4j.core.terminal.control.*;

/**
 * The main entry class for the native terminal device for Serial4j api.
 * 
 * @author pavl_g.
 */
public final class TerminalDevice {

    private static final Logger LOGGER = Logger.getLogger(TerminalDevice.class.getName());
    private final NativeTerminalDevice nativeTerminalDevice = new NativeTerminalDevice();
    
    private Permissions permissions = Permissions.O_RDWR.append(Permissions.O_NOCTTY)
                                                        .append(Permissions.O_NONBLOCK);
    private String permissionsDescription;
    private boolean loggingEnabled;
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * Instantiates a Unix terminal device object.
     */
    public TerminalDevice() {
    }

    /**
     * Sets up the jvm pointer from jni environment pointer on the heap for the global 
     * multithreaded reference.
     *
     * <p>
     * Note: Native and jvm threads can access the jni pointer from the global jvm pointer without
     * referencing one of the local jni env pointers stored on another thread stack (as this leads to a thread-transition error).
     * </p>
     */
    @Deprecated
    private static void setupJniEnvironment() {
        final int errno = NativeTerminalDevice.setupJniEnvironment();
        ErrnoToException.throwFromErrno(errno);
    }

    /**
     * Opens this device's serial port using the path from [serialPort] instance.
     *
     * @param serialPort the serial port instance to open.
     *
     * @throws NoSuchDeviceException if an attempt is made to open a non-device file.
     * @throws NoSuchFileException if an attempt is made to access a file that doesn't exist.
     * @throws FileAlreadyOpenedException if an attempt is made to open an already opened terminal device.
     * @throws InterruptedSystemCallException if there is a process interruption while opening the port.
     * @throws FileIsDirectoryException if an attempt is made to open a directory instead of a device.
     * @throws TooManyOpenedFilesException if an attempt is made to open too many devices exceeding the system limit.
     * @throws FileTableOverflowException if an attempt is made to open a device while system io is halt.
     * @throws NoSpaceLeftException if an attempt is made to open a device while there is no space left.
     * @throws ReadOnlyFileSystemException if an attempt is made to open a read-only device.
     * @throws PermissionDeniedException if an unauthorized user attempts to open this device.
     */
    public void openPort(final SerialPort serialPort) throws NoSuchDeviceException,
                                                             NoSuchFileException,
                                                             FileAlreadyOpenedException,
                                                             InterruptedSystemCallException,
                                                             FileIsDirectoryException,
                                                             TooManyOpenedFilesException,
                                                             FileTableOverflowException,
                                                             NoSpaceLeftException,
                                                             ReadOnlyFileSystemException,
                                                             PermissionDeniedException {                                                           
        if (isSerial4jLoggingEnabled()) {
            LOGGER.log(Level.INFO, "Opening serial device " + serialPort.getPath());
        }
        this.nativeTerminalDevice.setSerialPort(serialPort);
        final int returnValue = nativeTerminalDevice.openPort(serialPort.getPath(), permissions.getValue());
        if (isOperationFailed(returnValue)) {
            ErrnoToException.throwFromErrno(nativeTerminalDevice.getErrno());
        }
        /* update port data natively */
        /* ... */
    }

    /**
     * Initializes this terminal device with the default flags and the default 
     * read timeout configuration.
     * 
     * @throws InvalidPortException if an attempt is made to initialize an invalid port (non-opened/still-closed device).
     * @throws FileNotFoundException if the device opened before has been ejected.
     * @throws NoAvailableTtyDevicesException if the port is closed while performing the initialization operation.
     */
    public void initTerminal() throws InvalidPortException,
                                     FileNotFoundException,
                                     NoAvailableTtyDevicesException {
        if (isSerial4jLoggingEnabled()) {
            LOGGER.log(Level.INFO, "Initializing serial device " + getSerialPort().getPath());
        }
        int returnValue = nativeTerminalDevice.initTerminal();
        if (isOperationFailed(returnValue)) {
            returnValue = nativeTerminalDevice.getErrno();
        }
        ErrnoToException.throwFromErrno(returnValue);
        /* get the java streams from the port after initializing it with the native terminal */
        inputStream = new FileInputStream(getSerialPort().getPath());
        outputStream = new FileOutputStream(getSerialPort().getPath());
    }

    /**
     * Adjusts the terminal control flag of the termios, the terminal control flag controls the 
     * characters behavior in the local terminal.
     * 
     * @param flag the terminal control flag to adjust.
     * @throws BadFileDescriptorException if the filedes is not a valid file descriptor.
     * @throws InvalidPortException if the port is null or has not been initialized yet.
     * @throws NotTtyDeviceException if the filedes is not associated with a terminal device.
     * @throws InvalidArgumentException if the value of the when argument is not valid, 
     *                                  or there is something wrong with the data in the termios-p argument.
     */
    public void setTerminalControlFlag(final TerminalControlFlag flag) throws BadFileDescriptorException,
                                                                              InvalidPortException,
                                                                              NotTtyDeviceException,
                                                                              InvalidArgumentException {
                                       
        int returnValue = nativeTerminalDevice.setTerminalControlFlag(flag.getValue());
        if (isOperationFailed(returnValue)) {
            returnValue = nativeTerminalDevice.getErrno();
        }
        ErrnoToException.throwFromErrno(returnValue);
    }

    /**
     * Adjusts the terminal local flag of the termios, the terminal local flag controls how the
     * terminal interprets the characters on the local console.
     * 
     * @param flag the local flag to adjust.
     * @throws BadFileDescriptorException if the filedes is not a valid file descriptor.
     * @throws InvalidPortException if the port is null or has not been initialized yet.
     * @throws NotTtyDeviceException if the filedes is not associated with a terminal device.
     * @throws InvalidArgumentException if the value of the when argument is not valid, 
     *                                  or there is something wrong with the data in the termios-p argument.
     */
    public void setTerminalLocalFlag(final TerminalLocalFlag flag) throws BadFileDescriptorException,
                                                                          InvalidPortException,
                                                                          NotTtyDeviceException,
                                                                          InvalidArgumentException {
        int returnValue = nativeTerminalDevice.setTerminalLocalFlag(flag.getValue());
        if (isOperationFailed(returnValue)) {
            returnValue = nativeTerminalDevice.getErrno();
        }
        ErrnoToException.throwFromErrno(returnValue);
    }

    /**
     * Adjusts the terminal input flag for the termios, the terminal input flag controls how 
     * the terminal interpret the characters at the input from the terminal device.
     * 
     * @param flag the terminal input flag to adjust.
     * @throws BadFileDescriptorException if the filedes is not a valid file descriptor.
     * @throws InvalidPortException if the port is null or has not been initialized yet.
     * @throws NotTtyDeviceException if the filedes is not associated with a terminal device.
     * @throws InvalidArgumentException if the value of the when argument is not valid, 
     *                                  or there is something wrong with the data in the termios-p argument.
     */
    public void setTerminalInputFlag(final TerminalInputFlag flag) throws BadFileDescriptorException,
                                                                          InvalidPortException,
                                                                          NotTtyDeviceException,
                                                                          InvalidArgumentException {
        int returnValue = nativeTerminalDevice.setTerminalInputFlag(flag.getValue());
        if (isOperationFailed(returnValue)) {
            returnValue = nativeTerminalDevice.getErrno();
        }
        ErrnoToException.throwFromErrno(returnValue);
    }

    /**
     * Adjusts the terminal output flag for this terminal device, the terminal output flag controls
     * how the terminal interpret the charachters at the output to the terminal device.
     * 
     * @throws BadFileDescriptorException if the filedes is not a valid file descriptor.
     * @throws InvalidPortException if the port is null or has not been initialized yet.
     * @throws NotTtyDeviceException if the filedes is not associated with a terminal device.
     * @throws InvalidArgumentException if the value of the when argument is not valid, 
     *                                  or there is something wrong with the data in the termios-p argument.
     */
    public void setTerminalOutputFlag(final TerminalOutputFlag flag) throws BadFileDescriptorException,
                                                                          InvalidPortException,
                                                                          NotTtyDeviceException,
                                                                          InvalidArgumentException {
        int returnValue = nativeTerminalDevice.setTerminalOutputFlag(flag.getValue());
        if (isOperationFailed(returnValue)) {
            returnValue = nativeTerminalDevice.getErrno();
        }
        ErrnoToException.throwFromErrno(returnValue);
    }

    public TerminalControlFlag getTerminalControlFlag() throws BadFileDescriptorException,
                                                               InvalidPortException,
                                                               NotTtyDeviceException {
        final TerminalControlFlag TCF = TerminalControlFlag.EMPTY_INSTANCE;                                            
        final int returnValue = nativeTerminalDevice.getTerminalControlFlag();
        /* Warning: Force cast the errno to (int) */
        ErrnoToException.throwFromErrno((int) returnValue);
        TCF.setValue(returnValue);
        return TCF;                                        
    }

    public TerminalLocalFlag getTerminalLocalFlag() throws BadFileDescriptorException,
                                                            InvalidPortException,
                                                            NotTtyDeviceException {
        final TerminalLocalFlag TLF = TerminalLocalFlag.EMPTY_INSTANCE;                                            
        final int returnValue = nativeTerminalDevice.getTerminalLocalFlag();
        /* Warning: Force cast the errno to (int) */
        ErrnoToException.throwFromErrno(returnValue);
        TLF.setValue(returnValue);
        return TLF;                                        
    }

    public TerminalInputFlag getTerminalInputFlag() throws BadFileDescriptorException,
                                                            InvalidPortException,
                                                            NotTtyDeviceException {
        final TerminalInputFlag TIF = TerminalInputFlag.EMPTY_INSTANCE;
        final int returnValue = nativeTerminalDevice.getTerminalInputFlag();
        /* Warning: Force cast the errno to (int) */
        ErrnoToException.throwFromErrno((int) returnValue);
        TIF.setValue(returnValue);
        return TIF;                                        
    }

    public TerminalOutputFlag getTerminalOutputFlag() throws BadFileDescriptorException,
                                                            InvalidPortException,
                                                            NotTtyDeviceException {
        final TerminalOutputFlag TOF = TerminalOutputFlag.EMPTY_INSTANCE;
        final int returnValue = nativeTerminalDevice.getTerminalOutputFlag();
        /* Warning: Force cast the errno to (int) */
        ErrnoToException.throwFromErrno((int) returnValue);
        TOF.setValue(returnValue);
        return TOF;                                        
    }

    public void setPermissions(final Permissions permissions) {
        this.permissions = permissions;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setBaudRate(final BaudRate baudRate) throws BadFileDescriptorException,
                                                            InvalidPortException,
                                                            NotTtyDeviceException {
        if (isSerial4jLoggingEnabled()) {
            LOGGER.log(Level.INFO, "Setting device baud rate to " + baudRate.getRealBaud());
        }
        int returnValue = nativeTerminalDevice.setBaudRate(baudRate.getBaudRate());
        if (isOperationFailed(returnValue)) {
            returnValue = nativeTerminalDevice.getErrno();
        }
        ErrnoToException.throwFromErrno(returnValue);
    } 

    public void setReadConfigurationMode(final ReadConfiguration readConfiguration, final int timeoutValue, final int minimumBytes) throws NoSuchDeviceException,
                                                                                                                                                PermissionDeniedException,
                                                                                                                                                BrokenPipeException,
                                                                                                                                                InvalidPortException,
                                                                                                                                                NoAvailableTtyDevicesException {
         
        if (isSerial4jLoggingEnabled()) {
            LOGGER.log(Level.INFO, "Setting reading config to " + readConfiguration.getDescription());
        }
        final short timeoutByteValue = (short) (readConfiguration.getMode()[0] * timeoutValue);
        final short minimumBytesValue = (short) (readConfiguration.getMode()[1] * minimumBytes);
        final int errno = nativeTerminalDevice.setReadConfigurationMode((short) Math.min(255, timeoutByteValue),
                (short) Math.min(255, minimumBytesValue));
        ErrnoToException.throwFromErrno(errno);
    }

    public ReadConfiguration getReadConfigurationMode() {
        return ReadConfiguration.getFromNativeReadConfig(nativeTerminalDevice.getReadConfigurationMode());
    }
    
    public long write(final String buffer) throws NoSuchDeviceException,
                                                          PermissionDeniedException,
                                                          BrokenPipeException,
                                                          InvalidPortException,
                                                          NoAvailableTtyDevicesException {
        final long numberOfWrittenBytes = nativeTerminalDevice.write(buffer, buffer.length());
        String message;
        if (numberOfWrittenBytes == -1) {
            message = "Write Permission [O_WRONLY] isn't granted, [Permissions: " + permissionsDescription + "]";
        } else {
            message = "Invalid Port " + nativeTerminalDevice.getSerialPort().getPath(); 
        }
        if (numberOfWrittenBytes < 1) {
            ErrnoToException.throwFromErrno(nativeTerminalDevice.getErrno());
        }
        return numberOfWrittenBytes;
    }

    public long write(final int data) throws NoSuchDeviceException,
                                                 PermissionDeniedException,
                                                 BrokenPipeException,
                                                 InvalidPortException,
                                                 NoAvailableTtyDevicesException {
        final long numberOfWrittenBytes = nativeTerminalDevice.write(data);
        String message;
        if (numberOfWrittenBytes == -1) {
            message = "Write Permission [O_WRONLY] isnot granted.";
        } else {
            message = "Invalid Port " + nativeTerminalDevice.getSerialPort().getPath(); 
        }
        if (numberOfWrittenBytes <= 0) {
            ErrnoToException.throwFromErrno(nativeTerminalDevice.getErrno());
        }
        return numberOfWrittenBytes;
    }

    public long write(final int[] data) throws NoSuchDeviceException,
                                                   PermissionDeniedException,
                                                   BrokenPipeException,
                                                   InvalidPortException,
                                                   NoAvailableTtyDevicesException {
        long numberOfWrittenBytes = 0;
        for (int i = 0; i < data.length; i++) {
           numberOfWrittenBytes += this.write(data[i]);
           if (numberOfWrittenBytes <= 0) {
               break;
           }
        }
        return numberOfWrittenBytes;
    }

    public long read() {
        return nativeTerminalDevice.read();
    }

    public String getReadBuffer() {
        return nativeTerminalDevice.getReadBuffer();
    }

    public int getBaudRate() throws NoSuchDeviceException,
                                    PermissionDeniedException,
                                    BrokenPipeException,
                                    InvalidPortException,
                                    NoAvailableTtyDevicesException {
        if (isSerial4jLoggingEnabled()) {
            LOGGER.log(Level.INFO, "Getting device baud");
        }
        final int errno = nativeTerminalDevice.getBaudRate();
        ErrnoToException.throwFromErrno(errno);
        return errno;
    }

    public final String[] getSerialPorts() throws NoSuchDeviceException,
                                                  PermissionDeniedException,
                                                  BrokenPipeException,
                                                  InvalidPortException,
                                                  NoAvailableTtyDevicesException {
        fetchSerialPorts();
        return nativeTerminalDevice.getSerialPorts();
    }

    public void throwExceptionFromNativeErrno() throws NoSuchDeviceException,
                                                       PermissionDeniedException,
                                                       BrokenPipeException,
                                                       InvalidPortException,
                                                       NoAvailableTtyDevicesException {                                                
        final int errno = nativeTerminalDevice.getErrno();
        ErrnoToException.throwFromErrno(errno);
    }

    public void closePort() throws NoSuchDeviceException,
                                   PermissionDeniedException,
                                   BrokenPipeException,
                                   InvalidPortException,
                                   NoAvailableTtyDevicesException {
        if (isSerial4jLoggingEnabled()) {
            LOGGER.log(Level.INFO, "Closing port: " + getSerialPort().getPath());
        }
        final int errno = nativeTerminalDevice.closePort();
        ErrnoToException.throwFromErrno(errno);
    }
    
    public void setSerial4jLoggingEnabled(final boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public boolean isSerial4jLoggingEnabled() {
        return loggingEnabled;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public SerialPort getSerialPort() {
        return nativeTerminalDevice.getSerialPort();
    }

    private void fetchSerialPorts() throws NoSuchDeviceException,
                                           PermissionDeniedException,
                                           BrokenPipeException,
                                           InvalidPortException,
                                           NoAvailableTtyDevicesException {
        if (isSerial4jLoggingEnabled()) {
            LOGGER.log(Level.INFO, "Fetching Serial ports.");
        }
        final int errno = nativeTerminalDevice.fetchSerialPorts();
        ErrnoToException.throwFromErrno(errno);
    }

    private boolean isOperationFailed(final int returnValue) {
        return returnValue == Errno.ERR_OPERATION_FAILED.getValue();
    }
    
    private boolean isErrnoAvailable(final int errno) {
        return errno > 0;
    }
}