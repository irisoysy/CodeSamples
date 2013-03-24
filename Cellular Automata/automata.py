import string
import sys

def makeLengthProper(inString,desiredLen):
	'''takes in an input string inString. If the length of inString
	is less than the desiredLen integer, makeLengthProper adds the required
	number of zeros to the front of inString to make it the desired length

	The function does not reduce inString to a smaller length if the desired
	length is shorter than the actual length, nor does it required inString
	to be a string of numbers.

	>>> [makeLengthProper('hello',x) for x in [5,6,7,8,9]]
	['hello', '0hello', '00hello', '000hello', '0000hello']

	>>> makeLengthProper('0110',8)
	'00000110'

	'''

	x = len(inString)
	extra = ''
	if x<desiredLen:
		i = 0
		dif = desiredLen-x
		while i<dif:
			extra = extra+'0'
			i+=1

	return extra+inString

def makeRuleLookup(rule):
	'''Makes a hashtable which has length-3 bit strings as keys, and corresponding
	single bit strings dictated by the automaton rule input integer as values.

	>>> makeRuleLookup(1)['000']
	'1'

	>>> makeRuleLookup(30)['100']
	'1'

	>>> makeRuleLookup(30)['111']
	'0'
	'''

	ruleCode = makeLengthProper(bin(rule)[2:],8)

	i = 0
	ruleInput = []
	while i<8:
		inString = makeLengthProper(bin(i)[2:],3)
		ruleInput.insert(0,inString)
		i+=1

	i = 0
	table = {}
	while i<8:
		table[ruleInput[i]] = ruleCode[i]
		i+=1

	return table

def runTimeStep(ruleTable,prevRow):
	'''prevRow is a string representing the row at t = n. Function 
	uses ruleTable hashtable to make a new string corresponding to the 
	row at t = n+1 and returns it.

	>>> [runTimeStep(makeRuleLookup(30),x) for x in['00100','101010','010']]
	['01110', '101011', '111']
	'''

	n = len(prevRow)
	i = 0
	inPut = ''
	fullString = ''
	while i<n:
		if i==0:
			inPut = '0'+prevRow[i:i+2]
		elif i==n-1:
			inPut = prevRow[i-1:]+'0'
		else:
			inPut = prevRow[i-1:i+2]
		fullString = fullString+ruleTable[inPut]
		i+=1

	return fullString #string of the output line

rule = int(sys.argv[1])
n = int(sys.argv[2]) #n columns on both side of the center column (401x201 output)

ruleTable = makeRuleLookup(rule)

firstPart = makeLengthProper('',n)
prevRow = firstPart+'1'+firstPart

print 'P1 '+str(2*n+1)+' '+str(n)
print prevRow

i = 0
while i<n:
	prevRow = runTimeStep(ruleTable,prevRow)
	print prevRow
	i+=1

if __name__=="__main__":
	import doctest
	doctest.testmod()