grammar pens;

program: stat* EOF; // Zero or more repetitions of stat

stat: pen # Decla;

pen:
	'(' ID ',' 'status=' STATUS? ',' 'posx=' posX? ',' 'posy=' posY? ',' 'heading=' DEGREE? ','
		'colour=' COLOUR? ',' 'thickness=' thickness? ',' 'pattern=' PATTERN? ')' # PenDefinition;

posX: expr;
posY: expr;
thickness: expr;

expr:
	op = '-' e2 = expr						# ExprMinus
	| e1 = expr op = ('*' | '/') e2 = expr	# ExprMultDivMod
	| e1 = expr op = ('+' | '-') e2 = expr	# ExprAddSub
	| '(' expr ')'							# ExprParent
	| NUMBER								# ExprNumber
	| STATUS								# ExprStatus
	| COLOUR								# ExprColour
	| DEGREE								# ExprDegree
	| PATTERN								# ExprPattern;

STATUS: 'up' | 'down';
NUMBER: [0-9]+ ('.' [0-9]+)?;
HEX: [0-9a-fA-F];
COLOUR:
	'rgb ' NUMBER ' ' NUMBER ' ' NUMBER
	| '#' HEX HEX HEX HEX HEX HEX
	| LITERALCOLOUR;
PATTERN: 'solid' | 'dotted' | 'dashed';
ID: [a-z][a-zA-Z0-9_]*;
LITERALCOLOUR: (
		'black'
		| 'silver'
		| 'grey'
		| 'white'
		| 'maroon'
		| 'red'
		| 'purple'
		| 'fuchsia'
		| 'green'
		| 'lime'
		| 'olive'
		| 'yellow'
		| 'navy'
		| 'blue'
		| 'teal'
		| 'aqua'
	);
DEGREE: NUMBER 'º';

WS: [ \t\r\n]+ -> skip;
COMMENT: '###' .*? '\n' -> skip;
NEWLINE: '\r'? '\n' -> skip;
