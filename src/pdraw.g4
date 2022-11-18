grammar pdraw;

program:
    stat *EOF   // Zero or more repetitions of stat
    ;

stat:
    COMMENT #Comment
    | 'import' STRING 'as' ID #Import
    | declaration #Decla
    | attribution #Attr
    | op=('raise'|'lower') ID #PenCommand
    | ID action    #DoAction
    | 'if' expr 'then' '{' stat* '}'  elif*  els?  #IfElifElseStat
    | 'for' ID 'in' (r='range')? expr 'do' '{' stat* '}'   #ForLoop
    | 'while' expr 'do' '{' stat* '}'   #WhileLoop
    | 'function' ID '('((arg)(','arg)*)?')' 'do' '{' stat* ('return' expr)? '}' #Function
    | 'print(' expr ')' #print
    | 'add' expr 'to' ID    #arrayAdd
    | 'remove' expr 'from' ID   #arrayRemove
    | ID ID #IDID
    | expr #goExpr
    ;

arg:
    TYPE ID
    ;

action:
    'move to' '(' expr ',' expr ')'  #PenMoveTo
    | 'move by' '(' expr (','expr)? ')'  #PenMoveBy
    | 'rotate by' expr #PenRotateBY
    | 'change' chg+ #PenChangeTo
    ;

elif:
    ('elif' expr   'then' '{' stat* '}')
    ;

els:
    ('else' 'then' '{' stat* '}')
    ;

declaration:
    TYPE ID  ('=' expr)?
    ;

chg:
    prop=('-heading' | '-status' | '-posx' | '-posy' | '-colour'| '-thickness' | '-pattern') '=' expr
    ;

attribution:
    ID '=' expr
    ;

expr:
    '('(('status='STATUS|'posx='posx=expr|'posy='posy=expr|'heading='DEGREE|'colour='COLOUR|'thickness='thickness=expr|'pattern='PATTERN)(',''status='STATUS|',''posx='posx=expr|',''posy='posy=expr|',''heading='DEGREE|',''colour='COLOUR|',''thickness='thickness=expr|',''pattern='PATTERN)*)?')' #ExprPen
    | action    #ExprAction
    | '['(expr(','expr)*)?']' #ExprArray
    | '(' expr ')'    #ExprParenthesis
    | '-' expr      #ExprMinus
    | expr '^' expr #ExprExponent
    | expr op=('*'|'/'|'%') expr    #ExprMultDivMod
    | expr op=('+'|'-') expr #ExprPlusMinus
    | '!' expr  #ExprNot
    | expr op=('and'|'or') expr   #ExprAndOR
    | expr '==' expr    #ExprEquals
    | 'get' expr 'from' ID  #ExprGet
    | 'length of' ID    #ExprLength
    | op=('input num'| 'input')'('STRING?')' #ExprInput
    | ID'('(expr(','expr)*)?')' #ExprFunction
    | NUMBER  #ExprNumber
    | STATUS #ExprStatus
    | BOOLEAN   #ExprBoolean
    | COLOUR #ExprColour
    | STRING #ExprString
    | DEGREE #ExprDegree
    | PATTERN #ExprPattern
    | ID       #ExprID
    ;


COMMENT: ('###' .*? '\n' | '#*' .*? '*#') -> skip;
TYPE: ('array-')*('colour' | 'status' | 'pattern' | 'num' | 'string' | 'pen' | 'boolean' | 'degree' | 'action');
STATUS: 'up' | 'down';
COLOUR: 'rgb ' NUMBER ' ' NUMBER ' ' NUMBER | '#' HEX HEX HEX HEX HEX HEX | LITERALCOLOUR;
BOOLEAN: 'true'|'false';
NUMBER: [0-9]+ ('.' [0-9]+)?;
STRING: '"' .*? '"';
PATTERN: 'solid' | 'dotted' | 'dashed';
ID: [a-z][a-zA-Z0-9_]*;
HEX: [0-9a-fA-F]+;
DEGREE: NUMBER'º';

LITERALCOLOUR: ( 'black' | 'silver' | 'grey' | 'white' | 'maroon' | 'red' | 'purple' | 'fuchsia' | 'green' | 'lime' | 'olive' | 'yellow' | 'navy' | 'blue' | 'teal' | 'aqua');

WS: [ \t\r\n]+ -> skip;