/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: prime                         *
 *  DATE: Sun Apr 18 21:19:23 PDT 2021  *
 *                                      *
 ****************************************/

/*************
 *           *
 * EXTERNALS *
 *           *
 *************/
.extern printf
.extern __aeabi_idiv

/***************
 *             *
 * ENTRY POINT *
 *             *
 ***************/
.global main

/********
 *      *
 * MAIN *
 *      *
 ********/
.text
.balign 4
main:
	push { lr }
	mov fp, sp
	add sp, sp, #-4
	add sp, sp, #-4
	ldr r4, =13
	str r4, [fp, #-4]
	ldr r4, =2
	str r4, [fp, #-8]
	ldr r4, [fp, #-4]
	ldr r5, [fp, #-8]
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	ldr r4, =0
	cmp r6, r4
	bne end_1
	ldr r0, =ifmt
	ldr r4, [fp, #-4]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	b begin_1

.balign 4
end_1:
	ldr r4, =3
	str r4, [fp, #-8]

.balign 4
begin_2:
	ldr r4, [fp, #-8]
	ldr r5, [fp, #-4]
	cmp r4, r5
	bge end_2
	ldr r4, [fp, #-4]
	ldr r5, [fp, #-8]
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	ldr r4, =0
	cmp r6, r4
	bne end_3
	ldr r5, [fp, #-4]
	ldr r6, =3
	add r4, r5, r6
	str r4, [fp, #-8]
	b begin_3

.balign 4
end_3:
	ldr r5, [fp, #-8]
	ldr r6, =2
	add r4, r5, r6
	str r4, [fp, #-8]

.balign 4
begin_3:
	b begin_2

.balign 4
end_2:
	ldr r0, =ifmt
	ldr r4, [fp, #-4]
	mov r1, r4
	bl printf
	ldr r5, [fp, #-4]
	ldr r6, =3
	add r4, r5, r6
	ldr r5, [fp, #-8]
	cmp r4, r5
	bne end_4
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	b begin_4

.balign 4
end_4:
	ldr r0, =sfmt
	ldr r4, =str_3
	mov r1, r4
	bl printf

.balign 4
begin_4:

.balign 4
begin_1:

.text
.balign 4
quit:
	mov sp, fp
	pop { lr }
	mov r0, #0
	bx lr

/********************
 *                  *
 * STRING CONSTANTS *
 *                  *
 ********************/
.data
.balign 4
ifmt:
	.asciz "%d"

.balign 4
sfmt:
	.asciz "%s"

.balign 4
str_2:
	.asciz " is not prime.\n"

.balign 4
str_3:
	.asciz " is prime.\n"

