#define __concat_nx(a,b) a ## b
#define __concat_nx3(a,b,c) a ## b ## c
#define __concat_nx4(a,b,c,d) a ## b ## c ## d
#define __concat_nx5(a,b,c,d,e) a ## b ## c ## d ## e
#define CONCAT(a,b) __concat_nx(a,b)
#define CONCAT3(a,b,c) __concat_nx3(a,b,c)
#define CONCAT4(a,b,c,d) __concat_nx4(a,b,c,d)
#define CONCAT5(a,b,c,d,e) __concat_nx5(a,b,c,d,e)
#define __str_nx(a) #a
#define STR(a) __str_nx(a)

#define __DLGP__ Dlgp

#define __GRAAL_VERSION__ 0.8.7

