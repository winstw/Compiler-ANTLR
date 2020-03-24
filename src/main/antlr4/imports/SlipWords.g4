// lexer grammar SlipWords;

lexer grammar SlipWords;

BOOLEANTYPE : 'boolean';
INTEGERTYPE : 'integer';
CHARTYPE : 'char';
VOIDTYPE : 'void';

fragment DIGIT : '0'..'9';

fragment LETTER : 'A'..'Z' | 'a'..'z';

NAT : DIGIT+;

CHAR : '\'' (DIGIT | LETTER | ':' | '.' | '&' | '/' | '\\' | ';')+ '\'';

ID : LETTER (LETTER | DIGIT)* ;

COMMENT : ('/*' (.*?) '*/' | '//' .*? ('\r'?'\n' | EOF)) -> skip;

WS : [ \t\n]+ -> skip;

FILENAME : '"'ID'.map''"';

STRING : '"'(~[\\,\r\n])+'"';
