lexer grammar SlipWords;

DIGIT : '0'..'9';
INT : ('-')? (DIGIT)+;

STRING : '"' (~[\\,\r\n])+ '"';
CHAR : '\'' (DIGIT | LETTER | ':' | '.' | '&' | '/' | '\\' | ';')+ '\'';
// BOOLEAN : 'true' | 'false';
ID: LETTER (LETTER | DIGIT)* ;
LETTER : [a-zA-Z];


COMMENT : ('/*' (.*?) '*/' | '//' .*?'\r'?'\n') -> skip;
WS: [ \t\n]+ -> skip;
