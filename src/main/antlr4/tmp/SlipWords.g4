// lexer grammar SlipWords;

// DIGIT : '0'..'9';
// INT : ('-')? (DIGIT)+;


// CHAR : '\'' (DIGIT | LETTER | ':' | '.' | '&' | '/' | '\\' | ';')+ '\'';
// // BOOLEAN : 'true' | 'false';

// LETTER : [a-zA-Z];
// ID: LETTER (LETTER | DIGIT)* ;

// COMMENT : ('/*' (.*?) '*/' | '//' .*?'\r'?'\n') -> skip;
// WS: [ \t\n]+ -> skip;
// STRING : '"' (~[\\,\r\n])+ '"';



lexer grammar SlipWords;


DIGIT : '0'..'9';

LETTER : 'A'..'Z' | 'a'..'z';

INT : ('-')? (DIGIT)+;

CHAR : '\'' (DIGIT | LETTER | ':' | '.' | '&' | '/' | '\\' | ';')+ '\'';

ID: LETTER (LETTER | DIGIT)* ;

COMMENT : ('/*' (.*?) '*/' | '//' .*?'\r'?'\n') -> skip;

WS: [ \t\n]+ -> skip;

FILENAME : ID'.map';

STRING: '"'(~[\\,\r\n])+'"';
