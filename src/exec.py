import turtle
import math
import pensMain

turtle.colormode(255)
tur=turtle.Turtle(visible=False)
tur.speed(0)

def intOrFloat(num: float):
	if num%1==0:
		return int(num)
	else:
		return num

def calcDist(pen: dict, posX: float, posY: float):
	x=posX-pen["posX"]
	y=posY-pen["posY"]
	dist=math.sqrt((x)**2+(y)**2)
	ori=math.atan(y/x)
	tur.seth(ori*180//math.pi)
	return dist

def dashed(unit: int):
	tur.down()
	i=[]
	while(unit>0):
		i0=[0,0]
		ind=0
		while(unit>0):
			if(ind<5*tur.width()):
				i0[0]+=1
			elif(ind <10*tur.width()):
				i0[1]+=1
			else:
				break
			ind+=1
			unit-=1
		i.append(tuple(i0))

	for a in i:
		tur.forward(a[0])
		tur.up()
		tur.forward(a[1])
		tur.down()

def dotted(pen,unit: int):
	tur.up()
	i=[]
	while(unit>0):
		ind=0
		while(unit>0):
			if(ind<3*tur.width()):
				ind+=1
			else:
				break
			unit-=1
		i.append(ind)

	for a in i:
		tur.dot(pen["thickness"],pen["colour"])
		tur.forward(a)

def move(var, method:str, cord1:float, cord2:float):
	if type(var)==dict:
		moveOne(var, method, cord1, cord2)
	elif type(var)==list:
		for pen in var:
			moveOne(pen, method, cord1, cord2)

def moveOne(pen: dict, method:str, cord1:float, cord2:float):
	tur.up()
	tur.goto(pen["posx"], pen["posy"])
	tur.seth(pen["heading"])
	tur.color(pen["colour"])
	tur.width(width=pen["thickness"])
	if pen["status"]=="down":
		tur.down()
	if method=="by":
		tur.seth(cord2)
		if pen["status"]=="down":
			if pen["pattern"]=="solid":
				tur.forward(cord1)
			elif pen["pattern"]=="dotted":
				dotted(pen, cord1)
			else:
				dashed(cord1)
		else:
			tur.forward(cord1)
	else:
		if pen["status"]=="down":
			if pen["pattern"]=="solid":
				tur.goto(cord1, cord2)
			elif pen["pattern"]=="dotted":
				dotted(pen,calcDist(pen, cord1, cord2))
			else:
				dashed(calcDist(pen, cord1, cord2))
		else:
			tur.goto(cord1, cord2)
	ps=tur.pos()
	pen["posx"]=ps[0]
	pen["posy"]=ps[1]

	
var_a = 0
var_b = 1
var_square_a = var_a
var_square_b = var_b
var_p1 = { 'status':'up', 'posx':0, 'posy':0, 'heading':0, 'colour':'black', 'thickness':2, 'pattern':'solid'}
var_p1['heading']=90
var_p1['colour']='red'
var_p1['pattern']='solid'

var_p1['status']='down'

print(var_p1)
var_i = 0
var_j = 0
var_n = 0
var_n = intOrFloat(float(input("Putas, quantas? (aconselha-se no mínimo 100, fica mais barato) ")))
for var_i in range((var_n)):
	var_fdwd = 3.14159*var_b/2
	print(var_fdwd)
	var_fdwd = var_fdwd/90
	print(var_fdwd)
	for var_j in range((90)):
		move(var_p1,'by',var_fdwd,var_p1['heading'])
		var_p1['heading']+=(1)

	var_temp = var_a
	var_a = var_b
	var_b = var_temp+var_b



turtle.done()