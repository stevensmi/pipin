Pipin - an IO and device control project for the Raspberry Pi
================================

16 channel LED control using a TLC59116
---------------------------------------

The TLC59116 is a highly integrated 16 channel PWM LED driver from Texas Instruments

A board which allows LEDs to be easily connected is available from ELV Elektronik in Germany.

http://www.elv.de/led-i2c-steuertreiber-16-kanaele-bausatz.html

Connection to the RPi
---------------------
 
The ELV board can be directly connected to the RPi GPIO pins.

RPi Pin		I2C connection (ST1/2 on ELV board)
3 (SDA1)	SDA
5 (SLC1)	SCL
25 (GND)	GND

For information on the RPi GPIO pins see: http://elinux.org/RPi_Low-level_peripherals.
All but the earliest RPi's are revision 2. So make sure you use the section "R-Pi PCB Revision 2 UPDATE"

Loading the i2c-dev module
--------------------------

To use an I2C bus from a user programs under Linux there is a special driver 'i2c_dev'. This
can be loaded with the command:
	sudo modprobe i2c_dev

The command:
	ls /dev/i2c*
should show two devices /dev/i2c-0 and /dev/i2c-1.  On a revision 2 RPi i2c-1 is the bus we have connected
up on the GPIO pin 3 and 5.

You need to be able to access the "i2c-1" device. For testing you can do this with the command:
	sudo chmod a+rw /dev/i2c-1
	

Testing I2C connection to RPi
-----------------------------

To test the TLC59116 I set it to the I2C bus address 0x6F. On the ELV board this is easy to configure
by placing all 4 jumpers across the connections J4 J5 J6 J7. This sets the address pins A0 A1 A2 A3 to 1.

The address of all the devices on an I2C bus can be shown with "i2cdetect" (see Software to install).

i2cdetect 1

	you should see the entry '6f' in the line starting with '60:'
	
Running Pipin to test the LEDs from Java
----------------------------------------

LEDTest tests the first 4 LED attached to the TLC59116. It continually changes the PWM values which control the amount
of current flowing through the LEDs. Over a period of about 2s the LEDs are made brighter and then darker. This cycle
repeats. To run the test in Java run the following in the Pipin folder:

	java -Djava.library.path=lib -classpath lib/pi4j-core-0.0.4.jar:testbin ttree/pipin/i2c/LEDTest

Now you should see the first 4 LEDS flashing.

In principle this command simple runs the LEDTest class which is in the testbin folder. However because Pipin uses
the pi4j-core to access the I2C bus this also needs to be in the classpath. For this to access the /dev/i2c-1 device it needs
to use a native Linux library which is also in the "lib" folder.


Compiling Pipin
---------------

Pipin includes a Eclipse Java project for easy compilation. You can do this on any computer that run Java and then
use the results to you RPi.

For easy testing the class files have already be created and are available in the "testbin" folder.

Software to install
-------------------

	Java
	----
	For Pipin I use the recently release Oracle JDK 8 early access release.
		https://blogs.oracle.com/java/entry/developer_preview_of_java_se
	This has the advantage that it is fast and works directly on "Wheezy" and ArchlinuxARM for the RPi. Both of which use
	the "hard float" ABI which is what is used by this developer preview.
	Unpack this JDK into a folder named "jdk1.8.0" in you home directory. So that the JDK can be used from the command line
	it is sufficient to export two environment variables.
		export JAVA_HOME=~/jdk1.8.0
		export PATH=$PATH:$JAVA_HOME/bin
	To make this permanent by adding the lines to you .bash_profile
	  

	i2c-tools
	---------
	The i2c-tools include the "i2cdetect" command.

		ArchLinuxARM: pacman -S i2c-tools
		Debian: apt-get install i2c-tools
		
Permanently allowing access to I2C devices from user programs
-------------------------------------------------------------

It is useful to be able to setup the Linux so that /dev/i2c-1 is permanently available and all users can access it.
This can be done with the following configuration changes.

	i2c_dev module
	--------------
		ArchLinuxARM: Create a file "/etc/modules-load.d/i2c.conf" containing:
			i2c_dev
		Debian: Add to the file '/etc/modules' the line:
			i2c_dev

	udev
	---- 
		Create a file "/etc/udev/rules.d/20-i2c_dev.rules" containing:
			KERNEL=="i2c-1", GROUP="users", MODE="0660"

Additional software
-------------------

	Pi4J
	----
	Pipin builds on the work of the Pi4J project. This does all the hard work of providing a simple Java interface
	to the i2c-device. To get this lowlever access Pi4J consists of two parts: a java library "pi4j-core", and the
	native Linux library "libpi4j.so". Both of these are included directly in Pipin for ease of use. Thanks for the
	Pi4J developers for their hard work. Pi4J is licensed under "The Apache Software License, Version 2.0".
	
Development
-----------

Pipin is available on GitHub. 
	https://github.com/stevensmi/pipin
    