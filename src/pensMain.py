import sys
from antlr4 import *
from matplotlib import lines
#print("ENTREI NA MAIN")
from pensLexer import pensLexer
from pensParser import pensParser
from Execute import Execute

def manageFile(arraypens):
    lst = []

    # abrir o file para converter o dict para array de dicts
    with open('pensVISITOR.txt') as f:
        for line in f:
            #print(line)
            dictxx = {}
            data = line.split(';', 1)
            #print("data -> ", data)
            data  = data[1].split(';')
            for s in data:
                info = s.split(':')
                #print(s)
                dictxx[info[0]] = info[1]
            lst.append(dictxx)
    print(lst)
    return lst




def main(argv):
    #print("ant there we go, MAIN MAIN")
    visitor0 = Execute()
    try:
        arraypens = argv[2]
    except:
        arraypens = ""
    input_stream = None
    if len(sys.argv) > 1:
        filename = sys.argv[1]
        #print("filename: ", filename)
        input_stream = FileStream(filename, encoding="utf-8")
        #print("\n\ninput_stream:\n", input_stream, "\n\n")
        lexer = pensLexer(input_stream)
        stream = CommonTokenStream(lexer)
        parser = pensParser(stream)
        tree = parser.program()
        if parser.getNumberOfSyntaxErrors() == 0:
            visitor0.visit(tree)

    else:
        for line in sys.stdin:
            input_stream = InputStream(line)
            lexer = pensLexer(input_stream)
            stream = CommonTokenStream(lexer)
            parser = pensParser(stream)
            tree = parser.program()
            if parser.getNumberOfSyntaxErrors() == 0:
                visitor0.visit(tree)

    # work with exported file
    #print("\n\nCalling managing function")
    return manageFile(arraypens)