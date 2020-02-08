grammar Slip;

import SlipWords;

program : impDecl
            ((varDecl)* | (fctDecl)* | (constDecl)* | (enumDecl)*)
            'main' 'as' 'function' '(' ')' ':' 'void'
            'do'
              ('dig()' | (instruction)*)
            'end'
            ;

instBlock : ((varDecl)* | (enumDecl)* | (constDecl)* | (structure)*) instruction+;

argList : ID (',' ID)* 'as' type (ID (',' ID)* 'as' type)*;

fctDecl : ID 'as' 'function' '(' (argList)? ')' ':' (scalar | 'void') 'do' (instBlock)+ 'end';

impDecl : '#import' FILENAME;

type : scalar
     | array
     | structure
     ;


scalar : 'boolean'
       | 'integer'
       | 'char'
       ;


array : scalar'[' (DIGIT)+ (',' (DIGIT)+)? ']';

structure: ID 'as' 'record' (varDecl)+ 'end';

varDecl  : ID (',' ID)* 'as' type ('=' initVar)? ';' ;

initVar  : 'true'
         | 'false'
         | INT
         | STRING
         | CHAR
         | exprEnt
         | exprBool
         | initArrays
         ;

initArrays: '(' (initVar (',' initVar)*)?')' ;

constDecl : 'const' ID 'as' type '=' initVar ';';

enumDecl : 'enum' ID '=' '(' ID (',' ID)* ')' ';' ;

// exprD : exprEnt
//       | STRING
//       | CHAR
//       | exprBool
//       | exprG
//       | ID'(' (exprD (','exprD)*)? ')'
//       | '(' exprD ')'
//       ;

// exprEnt : INT
//         | exprD '+' exprD
//         | exprD '-' exprD
//         | exprD '*' exprD
//         | exprD '/' exprD
//         | exprD '%' exprD
//         | '-' exprD
//         ;

// exprBool : 'true'
//          | 'false'
//          | exprD 'and' exprD
//          | exprD 'or' exprD
//          | exprD '<' exprD
//          | exprD '>' exprD
//          | exprD '=' exprD
//          | exprD '<=' exprD
//          | exprD '>=' exprD
//          | exprD '<>' exprD
//          ;


exprD :
      STRING      #string
      | CHAR       #char
// exprBool
      | 'true'     #true
      | 'false'    #false
      | exprD 'and' exprD  #andExpr
      | exprD 'or' exprD   #orExpr
      | exprD '<' exprD    # lessExpr
      | exprD '>' exprD    # greaterExpr
      | exprD '=' exprD    # eqExpr
      | exprD '<=' exprD   # lessEqExpr
      | exprD '>=' exprD   # greatEqExpr
      | exprD '<>' exprD   # notExpr

// exprEnt
      | INT                # intExpr
      | exprD '+' exprD    # plusExpr
      | exprD '-' exprD    # minusExpr
      | exprD '*' exprD    # timesExpr
      | exprD '/' exprD    # divExpr
      | exprD '%' exprD    # modExpr
      | '-' exprD          # unaryMinusExpr
      | exprG              # exprGExpr
      | ID'(' (exprD (','exprD)*)? ')' # funcExpr
      | '(' exprD ')'                  # parens
      ;

exprEnt : INT
        | exprD '+' exprD
        | exprD '-' exprD
        | exprD '*' exprD
        | exprD '/' exprD
        | exprD '%' exprD
        | '-' exprD
        ;

exprBool : 'true'
         | 'false'
         | exprD 'and' exprD
         | exprD 'or' exprD
         | exprD '<' exprD
         | exprD '>' exprD
         | exprD '=' exprD
         | exprD '<=' exprD
         | exprD '>=' exprD
         | exprD '<>' exprD
         ;

exprG : ID
      | ID'['exprD (','exprD)? ']'
      | exprG'.'ID
      ;


instruction: 'if' '(' exprD ')' 'then' instruction+ 'end'
           | 'if' '(' exprD ')' 'then' instruction+ 'else' instruction+ 'end'
           | 'while' '(' exprD ')' 'do' instruction+ 'end'
           | 'repeat' instruction+ 'until' '(' exprD ')' 'end'
           | 'for' ID ':=' exprD 'to' exprD 'do' instruction+ 'end'
           | exprG ':=' exprD ';'
           | actionType ';'
           ;

actionType : 'left(' (exprD)? ')'
           | 'right(' (exprD)? ')'
           | 'up(' (exprD)? ')'
           | 'down(' (exprD)? ')'
           | 'jump(' (exprD)? ')'
           | 'fight()'
           | 'dig()'
           ;

