from cmath import exp
import numbers
import matplotlib
from antlr4 import *
from pensParser import pensParser
from pensVisitor import pensVisitor


class Execute(pensVisitor):
    pensArray = []
    dicPen = {}
    stringPenx = ""
    ids = []
    idAtual = ""

    def visitProgram(self, ctx: pensParser.ProgramContext):
        file1 = open('pensVISITOR.txt', 'w')    # 'a' to append to the end of the file
        for a in range(len(ctx.stat())):
            self.visit(ctx.stat(a))
            # print(self.visit(ctx.stat(a)))
            if self.dicPen not in self.pensArray:
                self.pensArray.append(self.dicPen)
                file1.write(self.stringPenx)
            self.dicPen = {}    # voltar a ter dict vazio
            #print("debugging", self.stringPenx, "\n")
            self.stringPenx = ""
        #print("\npensLength: ", len(self.pensArray))

        file1.close()

    def visitDecla(self, ctx: pensParser.DeclaContext):
        return self.visitChildren(ctx)

    def getPattern(self, s):
        if(s == "solid"):
            return "0"
        elif(s == "dotted"):
            return "1"
        elif(s == "dashed"):
            return "2"

    def visitPenDefinition(self, ctx: pensParser.PenDefinitionContext):
        # status def
        self.idAtual = ctx.ID().getText()
        #print("idAtual: ", self.idAtual)
        if self.idAtual not in self.ids:
            self.ids.append(self.idAtual)
        else:
            print("ERRO, já existe uma caneta com o id = '{}' (não vai ser adicionada ao file)".format(self.idAtual))

        self.dicPen[self.idAtual] = []
        self.stringPenx +=self.idAtual + ";"
        # status to dictionary
        try:
            status = ctx.STATUS().getText()
            #print("PEN try {} STATUS {}: ".format(self.idAtual, status))
            self.dicPen[self.idAtual].append("'status':" + "'" + status + "'")
            self.stringPenx += ("'status':" + "'" + status + "';")

        except:
            status = "up"
            #print("PEN catch {} STATUS {}: ".format(self.idAtual, status))
            self.dicPen[self.idAtual].append("'status':" + status)
            self.stringPenx += ("'status':" + "'" + status + "';")

        # posx to dictionary
        try:
            posx = ctx.posX().getText()
            #print("PEN try {} posX {}: ".format(self.idAtual, posx))
            self.dicPen[self.idAtual].append("'posx':" + posx)
            self.stringPenx += ("'posx':" + posx) + ";"
        except:
            posx = '0'
            #print("PEN catch {} posX {}: ".format(self.idAtual, posx))
            self.dicPen[self.idAtual].append("'posx':" + posx)
            self.stringPenx += ("'posx':" + posx) + ";"

        # posY to dictionary
        try:
            posy = ctx.posY().getText()
            #print("PEN try {} posy {}: ".format(self.idAtual, posy))
            self.dicPen[self.idAtual].append("'posy':" + posy)
            self.stringPenx += ("'posy':" + posy) + ";"
        except:
            posy = '0'
            #print("PEN catch {} posy {}: ".format(self.idAtual, posy))
            self.dicPen[self.idAtual].append("'posy':" + posy)
            self.stringPenx += ("'posy':" + posy) + ";"

        # heading to dictionary
        try:
            orient = ctx.DEGREE().getText().split("º")[0]
            #print("PEN try {} orient {}: ".format(self.idAtual, orient))
            self.dicPen[self.idAtual].append("'heading':" + orient)
            self.stringPenx += ("'heading':" + orient) + ";"
        except:
            orient = '0'
            #print("PEN catch {} orient {}: ".format(self.idAtual, orient))
            self.dicPen[self.idAtual].append("'heading':" + orient)
            self.stringPenx += ("'heading':" + orient) + ";"

        # color to dictionary
        try:
            #print("cgsdg_ > ", ctx.COLOUR().getText())
            colour = ctx.COLOUR().getText()

            if (colour.startswith("rgb")):
                lista = colour.split(" ")
                #print("lista: ", lista)
                cl = "("+lista[1]+","+lista[2]+","+lista[3]+")"
                self.dicPen[self.idAtual].append("'colour':" + "'"+ cl + "'")
                self.stringPenx += ("'colour':" + cl) + ";"
            else:
                self.dicPen[self.idAtual].append("'colour':" + "'" + colour + "'")
                #print("PEN try {} colour {}: ".format(self.idAtual, colour))
                self.stringPenx += ("'colour':" + "'" + colour + "'") + ";"
        except:
            colour = "'black'"
            #print("PEN catch {} colour {}: ".format(self.idAtual, colour))
            self.dicPen[self.idAtual].append("'colour':'" + colour +"'")
            self.stringPenx += ("'colour':'" + colour +"'") + ";"

        # thickness to dictionary
        try:
            thickness = ctx.thickness().getText()
            #print("PEN try {} thickness {}: ".format(self.idAtual, thickness))
            self.dicPen[self.idAtual].append("'thickness':" + thickness)
            self.stringPenx += ("'thickness':" + thickness) + ";"
        except:
            thickness = "2"
            #print("PEN catch {} thickness {}: ".format(self.idAtual, thickness))
            self.dicPen[self.idAtual].append("'thickness':" + thickness)
            self.stringPenx += ("'thickness':" + thickness) + ";"

        # pattern to dictionary
        try:
            pattern = self.getPattern(ctx.PATTERN().getText())
            #print("PEN try {} pattern {}: ".format(self.idAtual, pattern))
            self.dicPen[self.idAtual].append("'pattern':" + pattern)
            self.stringPenx += ("'pattern':" + pattern+ "\n")
        except:
            pattern = "0"
            #print("PEN catch {} pattern {}: ".format(self.idAtual, pattern))
            self.dicPen[self.idAtual].append("'pattern':" + pattern)
            self.stringPenx += ("'pattern':" + pattern + "\n")
        return self.visitChildren(ctx)

    def visitPosX(self, ctx: pensParser.PosXContext):
        posx = self.visit(ctx.expr())
        #print("posx: ", posx)
        return posx

    def visitPosY(self, ctx: pensParser.PosYContext):
        posy = self.visit(ctx.expr())
        #print("posy: ", posy)
        return posy

    def visitThickness(self, ctx: pensParser.ThicknessContext):
        thickness = self.visit(ctx.expr())
        #print("thickness: ", thickness)
        return thickness

    def visitExprAddSub(self, ctx: pensParser.ExprAddSubContext):
        e1 = self.visit(ctx.e1)
        e2 = self.visit(ctx.e2)
        # print(e1)
        # print(e2)
        if (not e1 is None) and (not e2 is None):
            if ctx.op.text == "+":
                # print("result = ", e1 + e2)
                return e1 + e2
            else:
                # print("result = ", e1 - e2)
                return e1 - e2

    def visitExprPattern(self, ctx: pensParser.ExprPatternContext):
        # print("pattern Visitor")
        # print("idAtual : ", self.idAtual)
        pattern = ctx.PATTERN().getText()
        #print("pattern: ", pattern)
        # print("pattern :", pattern)
        const = "pattern="+pattern
        if const not in self.dicPen[self.idAtual]:
            self.dicPen[self.idAtual].append("pattern=" + pattern)
        # print(self.dicPen)
        return pattern

    def visitExprDegree(self, ctx: pensParser.ExprDegreeContext):
        #print("degree: ", self.visit(ctx.DEGREE().getText().split("º")[0]))
        return self.visit(ctx.DEGREE().getText().split("º")[0])

    def visitExprNumber(self, ctx: pensParser.ExprNumberContext):
        return float(ctx.NUMBER().getText())

    def visitExprStatus(self, ctx: pensParser.ExprStatusContext):
        print("status: ", self.visit(ctx.STATUS().getText))
        return self.visit(ctx.STATUS().getText)

    def visitExprColour(self, ctx: pensParser.ExprColourContext):
        # swtich rgb hex colour
        #print("VISITING COLOUR")
        s = ctx.COLOUR().getText()
        if (s.startswith("rgb")):
            numbersrgb = s.split(" ")
            return self.rgb_to_hex([int(numbersrgb[0]), int(numbersrgb[1]), int(numbersrgb[2])])
        else:
            return "'"+s+"'"

    def visitExprMultDivMod(self, ctx: pensParser.ExprMultDivModContext):
        e1 = self.visit(ctx.expr(0))
        e2 = self.visit(ctx.expr(1))
        if (not e1 is None) and (not e2 is None):
            if ctx.op.text == "*":
                # print(e1 * e2)
                return e1 * e2
            else:
                if e2 == 0:
                    print("ERROR: divide by zero!")
                else:
                    # print(e1 / e2)
                    return e1 / e2

    def visitExprMinus(self, ctx: pensParser.ExprMinusContext):
        e2 = self.visit(ctx.e2) * -1
        return e2

    def visitExprParent(self, ctx: pensParser.ExprParentContext):
        return self.visit(ctx.expr())
