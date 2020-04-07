// lexer grammar SlipWords;
lexer grammar SlipLexer;

//--------Words---------

// Groups
LPAR: '(';
RPAR: ')';
LBRACKET: '[';
RBRACKET: ']';

// Misc
COMMA: ',';
IMPORT: 'import';
CONST: 'const';
SEMICOLON: ';';
DOT: '.';
COLON: ':';
HASH: '#';

// Operation
PLUS: '+';
MINUS: '-';
TIMES: '*';
DIVIDE: '/';
MODULO: '%';

// Affecation
AFFECT: ':=';

// Comparaison
EQUAL: '=';
DIFF: '<>';
LT: '<';
GT: '>';
LTOE: '<=';
GTOE: '>=';
NOT: 'not';

// Boolean
TRUE: 'true';
FALSE: 'false';
AND: 'and';
OR: 'or';

// Types
BOOLEANTYPE : 'boolean';
INTEGERTYPE : 'integer';
CHARTYPE : 'char';
VOIDTYPE : 'void';
STRUCT: 'record';
ENUM: 'enum';

// Instructions
IF: 'if';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';
THEN: 'then';
UNTIL: 'until';
REPEAT: 'repeat';
TO: 'to';

// Actions
LEFT: 'left';
RIGHT: 'right';
UP: 'up';
DOWN: 'down';
JUMP: 'jump';
FIGHT: 'fight';
DIG: 'dig';

MAP: 'map' -> pushMode(ISLAND);
FUNCTION: 'function';
MAIN: 'main';
AS: 'as';
DO: 'do';
END: 'end';



fragment DIGIT : '0'..'9';

fragment LETTER : 'A'..'Z' | 'a'..'z';

NAT : DIGIT+;

CHAR : '\'' (DIGIT | LETTER | ':' | '.' | '&' | '/' | '\\' | ';')+ '\'';

ID : LETTER (LETTER | DIGIT)* ;

COMMENT : ('/*' (.*?) '*/' | '//' .*? ('\r'?'\n' | EOF)) -> skip;

WS : [ \t\n]+ -> skip;

FILENAME : '"'ID'.map''"';

STRING : '"'(~[\\,\r\n])+'"';

mode ISLAND;
NEWLINE : [\n];
MAP_CHAR: '@' | 'X' | 'G' | 'P' | 'A' | 'B' | 'T' | 'S' | '_' | 'Q';
SPACE: [ \t] -> skip;
MAP_NAT: NAT -> type(NAT);
MAP_COLON: COLON -> type(COLON);
MAP_COMMENT: COMMENT -> skip;