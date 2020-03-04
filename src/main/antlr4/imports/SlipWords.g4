// lexer grammar SlipWords;

lexer grammar SlipWords;



fragment DIGIT : '0'..'9';

fragment LETTER : 'A'..'Z' | 'a'..'z';

NAT: DIGIT+;

CHAR : '\'' (DIGIT | LETTER | ':' | '.' | '&' | '/' | '\\' | ';')+ '\'';

ID: LETTER (LETTER | DIGIT)* ;

COMMENT : ('/*' (.*?) '*/' | '//' .*?'\r'?'\n') -> skip;

WS: [ \t\n]+ -> skip;

FILENAME : '"'ID'.map''"';

STRING : '"'(~[\\,\r\n])+'"';
