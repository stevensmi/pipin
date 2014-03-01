Pipin - an IO and device control project for the Raspberry Pi
=============================================================

16 channel LED control using a TLC59116
---------------------------------------

The TLC59116 is a highly integrated 16 channel PWM LED driver from Texas Instruments

A board which allows LEDs to be easily connected is available from ELV Elektronik in Germany.

http://www.elv.de/led-i2c-steuertreiber-16-kanaele-bausatz.html

Dual DC motor control using the MD25 controller
-----------------------------------------------

The MD25 is an I2C interfaced dual DC motor control board from Robot Electronics (Daventech) UK.

The MD25 supplied on its own, or with two motors with integrated encoders. This allows you to quickly
setup a simple system and drive it with two motors.

http://www.robot-electronics.co.uk/

I2C connection to the Raspberry Pi
-------------------------
 
The ELV board and/or the MD25 can be directly connected to the Raspberry Pi GPIO pins.

RPi		I2C name		ELV board	MD25 board
GPIO pin				ST1/2		(any one of four connection blocks)	
3 (SDA1)	SDA			SDA		SDA
5 (SLC1)	SCL			SCL		SCL
25 (GND)	GND			GND		Ground

For information on the RPi GPIO pins see: http://elinux.org/RPi_Low-level_peripherals.
All but the earliest RPi's are revision 2. So make sure you use the section "R-Pi PCB Revision 2 UPDATE"

Loading the i2c-dev module
--------------------------

To use an I2C bus from a user program under Linux there is a special driver 'i2c_dev'. This
can be loaded with the command:
	sudo modprobe i2c_dev

The command:
	ls /dev/i2c*
should show two devices /dev/i2c-0 and /dev/i2c-1. On a revision 2 RPi, i2c-1 is the I2C bus with extenal connections
available on the GPIO pins 3 and 5.

You need to be able to access the "i2c-1" device from all users on the RPi. For testing you can do this with the command:
	sudo chmod a+rw /dev/i2c-1
	

Testing I2C connection on the Raspberry Pi
-----------------------------

To test the TLC59116, set it to used the I2C bus address 0xDE. On the ELV board this is easy to configure
by placing all 4 jumpers across the connections J4 J5 J6 J7. This sets the address pins A0 A1 A2 A3 to 1.

To test the MD25, set it to the I2C bus address 0xB0. This is the default for the MD25 board.

The address of all the devices on an I2C bus can be shown with "i2cdetect" (see "Software to install" later).

i2cdetect 1

	you should see the entry '6f' in the line starting with '60:' for the ELV board.
	you should see the entry '58' in the line starting with '50:' for the MD25 board.
	
	The "i2c-dev" system addresses are half those normally defined in I2C.
	So the I2C device address 0xDE becomes 0x6F, and 0xDE becomed 0x58 in the "i2c-dev" system!
	
Running Pipin to test the LEDs from Java
----------------------------------------

The Pipin program "LEDTest" tests the first 4 LEDs attached to the TLC59116. It continually changes the PWM values which control the amount of current flowing through the LEDs. Over a period of about 2s the LEDs are made brighter and then darker. This cycle repeats.

To run the test in Java run the following in the Pipin folder:

	java -jar LEDTest.jar

Now you should see the first 4 LEDs flashing as described.

Running Pipin to test the motors from Java
------------------------------------------

MD25Test tests both motors attached to the MD25. It simply prompts for a speed between -128 and 127 and then sets both motors to that speed.
After 2s the motors will be stopped again automatically by the MD25. 

	java -jar MD25Test.jar


The two examples simply start a Java program from the JAR files. To access the /dev/i2c-1 device Pipin used the pi4j-core library. This must be in the "lib" folder, which contains a JAR file and a Linux shared library.


Compiling Pipin
---------------

Pipin includes a Eclipse Java project and an "ant" script for easy compilation. With the "ant" script the complete Pipin project and examples can be compiled. Running "ant" in the Pipin folder will compile the examples and update the "jar" files. Alternatively you can do this on any computer that runs Java or has Eclipse installed and then use the results to you RPi.

Software to install
-------------------

	Java
	----
	Pipin works with the "Oracle JDK 7" for the Raspberry Pi. Simply install the package 'oracle-java7-jdk'
	on Raspbian. In addtion it is useful to install the "ant" package so that everything can easily be compiled.
	On other systems such as ArchLinuxARM the OpenJDK can be used.

	i2c-tools
	---------
	The "i2cdetect" command is part of the Linux "i2c-tools" which can be installed on ArchLinuxArm or Raspbian

		ArchLinuxARM: pacman -S i2c-tools
		Raspbian: apt-get install i2c-tools
		
Permanently allowing access to I2C devices from user programs
-------------------------------------------------------------

It is useful to be able to setup the Linux so that /dev/i2c-1 is permanently available and all users can access it.
This can be done with the following configuration changes.

	i2c_dev module
	--------------
		ArchLinuxARM: Create a file "/etc/modules-load.d/i2c.conf" containing:
			i2c_dev
		Raspbian: Add to the file '/etc/modules' the line:
			i2c_dev

	udev access rules
	----------------- 
		Create a file "/etc/udev/rules.d/20-i2c_dev.rules" containing:
			KERNEL=="i2c-1", GROUP="users", MODE="0660"

Scratch - providing remote sensors
----------------------------------

Scratch is a wonderful graphically programming systems for beginners. It is easy to use on the Raspberry Pi, slow but a lot of fun.
Scratch has a "Remote Sensors Protocol" see http://wiki.scratch.mit.edu/wiki/Remote_Sensors_Protocol. With this it is possible to program with real sensors, such as LEDs and motors, and not just with cat icons!

It is fairly easy in Java to implement the Scratch remote sensor protocol so outputs can be changed and inputs can be read. With Pipin, remote sensors have been implemented for both LEDs and the Motors. An example which automatically connects to scratch and sets up the two I2C devices is provided in 'ttree.scratch.ScratchRobo'. The command line and code are fairly self explanatory if you wish to experiment!

The example can be compiled with "ant ScratchRobo" and started with "java -java ScratchRobo localhost 58 6F". This assumes you have started Scratch, enabled remote sensors and have an MD25 and ELV LED board connected and configured as described in the previous examples.


Additional software
-------------------

	Pi4J
	----
	Pipin builds on the work of the Pi4J project. This does all the hard work of providing a simple Java interface
	to an i2c-dev devices. To get this low level access Pi4J consists of two parts: a java library "pi4j-core", and 	a native Linux library "libpi4j.so". Both of these are included directly in Pipin for ease of use. Thanks to 		the Pi4J developers for their hard work. Pi4J is licensed under "The Apache Software License, Version 2.0".
	
Development
-----------

Pipin is available on GitHub. 
	https://github.com/stevensmi/pipin
	
Copyright
---------
Copyright 2013,2014 Michael Stevens

License
-------
    This file is part of ttree distribution.

    ttree is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ttree is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ttree.  If not, see <http://www.gnu.org/licenses/>.
    
