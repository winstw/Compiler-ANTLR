grammar Slip;

import SlipWords;

type : scalar | array | structure;
scalar : 'boolean' | 'integer' | 'char';
array : scalar'[' (DIGIT)+ (',' (DIGIT)+)? ']';
structure: ID 'as' 'record' (vardecl)+ 'end';

vardecl: ID (',' ID)* 'as' type ('=' initvar)? ';' ;
initvar: 'true' | 'false' | INT | STRING | CHAR | exprent | exprbool | initarrays;
initarrays: '(' (initvar (',' initvar)*)?')' ;

constdecl : 'const' ID 'as' type '=' initvar ';';
enumdecl : 'enum' ID '=' '(' ID (',' ID)* ')' ';' ;

exprd : exprent
      | STRING
      | CHAR
      | exprbool
      | exprg
      | ID'(' (exprd (','exprd)*)? ')'
      | '(' exprd ')'
      ;

exprent : INT
        | exprd '+' exprd
        | exprd '-' exprd
        | exprd '*' exprd
        | exprd '/' exprd
        | exprd '%' exprd
        | '-' exprd
        ;

exprbool : 'true'
         | 'false'
         | exprd 'and' exprd
         | exprd 'or' exprd
         | exprd '<' exprd
         | exprd '>' exprd
         | exprd '=' exprd
         | exprd '<=' exprd
         | exprd '>=' exprd
         | exprd '<>' exprd
         ;

exprg : ID
      | ID'['exprd (','exprd)? ']'
      | exprg'.'ID
      ;


instruction: 'if' '(' exprd ')' 'then' instruction+ 'end'
           | 'if' '(' exprd ')' 'then' instruction+ 'else' instruction+ 'end'
           | 'while' '(' exprd ')' 'do' instruction+ 'end'
           | 'repeat' instruction+ 'until' '(' exprd ')' 'end'
           | 'for' ID ':=' exprd 'to' exprd 'do' instruction+ 'end'
           | exprg ':=' exprd ';'
           | actiontype ';'
           ;

actiontype : 'left(' (exprd)? ')'
           | 'right(' (exprd)? ')'
           | 'up(' (exprd)? ')'
           | 'down(' (exprd)? ')'
           | 'jump(' (exprd)? ')'
           | 'fight()'
           | 'dig()'
           ;

fctdecl : ID 'as' 'function' '(' (arglist)? '):' (scalar | 'void') 'do' (instblock)+ 'end';

arglist : ID (',' ID)* 'as' type (ID (',' ID)* 'as' type)*;

instblock : ((vardecl)* | (enumdecl)* | (constdecl)* | (structure)*) instruction+;


impdecl : '#import' '"'filedecl'"';

filedecl : filename'.map';
filename : LETTER (DIGIT | LETTER)*;

programme : impdecl
            ((vardecl)* | (fctdecl)* | (constdecl)* | (enumdecl)*)
            'main' 'as' 'function' '(' ')' ':' 'void'
            'do'
              ('dig()' | (instruction)*)
            'end'
            ;