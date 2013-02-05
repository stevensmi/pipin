# This code is copyright Simon Walters under GPL v2
# This code is derived from scratch_handler by Thomas Preston


from array import *
import threading
import socket
import time
import sys
import struct
import datetime as dt

import smbus

LEDS=16

# I2C LED Driver TLC59116
i2c_addr = 0x6e
bus=smbus.SMBus(1)

# Setup PWM output on all 16 LEDs
bus.write_byte_data(i2c_addr, 0x0, 0) # OSC On
mode=0xAA # PWM mode for output
bus.write_byte_data(i2c_addr, 0x14, mode)
bus.write_byte_data(i2c_addr, 0x15, mode)
bus.write_byte_data(i2c_addr, 0x16, mode)
bus.write_byte_data(i2c_addr, 0x17, mode)


def isNumeric(s):
    try:
        float(s)
        return True
    except ValueError:
        return False


PORT = 42001
DEFAULT_HOST = '127.0.0.1'
BUFFER_SIZE = 240
SOCKET_TIMEOUT = 1


class MyError(Exception):
    def __init__(self, value):
        self.value = value

    def __str__(self):
        return repr(self.value)

class ScratchSender(threading.Thread):
    def __init__(self, socket):
        threading.Thread.__init__(self)
        self.scratch_socket = socket
        self._stop = threading.Event()


    def stop(self):
        self._stop.set()

    def stopped(self):
        return self._stop.isSet()

    def run(self):
        return

class ScratchListener(threading.Thread):
    def __init__(self, socket):
        threading.Thread.__init__(self)
        self.scratch_socket = socket
        self._stop = threading.Event()

    def send_scratch_command(self, cmd):
        n = len(cmd)
        a = array('c')
        a.append(chr((n >> 24) & 0xFF))
        a.append(chr((n >> 16) & 0xFF))
        a.append(chr((n >>  8) & 0xFF))
        a.append(chr(n & 0xFF))
        self.scratch_socket.send(a.tostring() + cmd)


    def stop(self):
        self._stop.set()

    def stopped(self):
        return self._stop.isSet()

    def physical_led_update(self, led_index, value):
        if (led_index >= 0 and led_index < LEDS):
            bus.write_byte_data(i2c_addr, 0x02 + led_index, value)

    def run(self):
        global cycle_trace
        #This is main listening routine
        while not self.stopped():
            try:
                data = self.scratch_socket.recv(BUFFER_SIZE)
                dataraw = data[4:].lower()
                #print 'Length: %d, Data: %s' % (len(dataraw), dataraw)
                #print 'Cycle trace' , cycle_trace
                if len(dataraw) == 0:
                    #This is probably due to client disconnecting
                    #I'd like the program to retry connecting to the client
                    #tell outer loop that Scratch has disconnected
                    if cycle_trace == 'running':
                        cycle_trace = 'disconnected'
                        break

            except socket.timeout:
                #print "No data received: socket timeout"
                continue

            print dataraw

            if 'sensor-update' in dataraw:

                #check for individual port commands
                sensor = dataraw[13:].split()
                for u in range(len(sensor) / 2):
                    what = sensor[u*2][1:][:-1]
                    if (what[0:3] == 'led'):
                        try:
                            led = int(what[3:])-1
                            value = int(sensor[u*2+1])
                            self.physical_led_update(led, value)
                        except ValueError:
                            print 'bad led values', sensor
    
            if 'broadcast "' in dataraw:
                broadcast = dataraw[11:][:-1]
                if ('allon' in broadcast):
                    for l in range(LEDS):
                      self.physical_led_update(l,255)
                elif ('alloff' in broadcast):
                    for l in range(LEDS):
                        self.physical_led_update(l,0)
                elif ('led' in broadcast):        
                    pp = broadcast.find('led')
                    values = broadcast[(pp+3):].split()
                    if (len(values) == 2):
                       self.physical_led_update(int(values[0])-1,int(values[1]))
                    
            if 'stop handler' in dataraw:
                cleanup_threads((listener, sender))
                sys.exit()

            #else:
                #print 'received something: %s' % dataraw


def create_socket(host, port):
    while True:
        try:
            print 'Trying'
            scratch_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            scratch_sock.connect((host, port))
            break
        except socket.error:
            print "There was an error connecting to Scratch!"
            print "I couldn't find a Mesh session at host: %s, port: %s" % (host, port)
            time.sleep(3)
            #sys.exit(1)

    return scratch_sock

def cleanup_threads(threads):
    for thread in threads:
        thread.stop()

    for thread in threads:
        thread.join()

if __name__ == '__main__':
    if len(sys.argv) > 1:
        host = sys.argv[1]
    else:
        host = DEFAULT_HOST

cycle_trace = 'start'
while True:

    if (cycle_trace == 'disconnected'):
        print "Scratch disconnected"
        for l in range(LEDS):
            listener.physical_led_update(l,0)
        cleanup_threads([listener])
        sys.exit(0)
#        cycle_trace = 'start'

    if (cycle_trace == 'start'):
        # open the socket
        print 'Starting to connect...' ,
        the_socket = create_socket(host, PORT)
        print 'Connected!'
        the_socket.settimeout(SOCKET_TIMEOUT)
        listener = ScratchListener(the_socket)
#        sender = ScratchSender(the_socket)
        cycle_trace = 'running'
        print "Running...."
        listener.start()
#        sender.start()

    # wait for ctrl+c
    try:
        time.sleep(1) # 1s

    except KeyboardInterrupt:
        cleanup_threads((listener,sender))
        sys.exit()

