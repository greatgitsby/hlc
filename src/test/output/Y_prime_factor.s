/****************************************
 *                                      *
 *  AUTO-GENERATED, DO NOT MODIFY       *
 *                                      *
 *  FILE: prime_factor                  *
 *  DATE: Sun Apr 18 21:01:26 PDT 2021  *
 *                                      *
 ****************************************/

/*************
 * EXTERNALS *
 *************/
.extern printf
.extern __aeabi_idiv

/***************
 * ENTRY POINT *
 ***************/
.global main

/********
 * MAIN *
 ********/
.text
.balign 4
main:
	push { lr }
	mov fp, sp
	add sp, sp, #-4
	add sp, sp, #-4
	ldr r4, =100
	str r4, [fp, #-4]
	ldr r4, =2
	str r4, [fp, #-8]
	ldr r0, =sfmt
	ldr r4, =str_2
	mov r1, r4
	bl printf
	ldr r0, =ifmt
	ldr r4, [fp, #-4]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_3
	mov r1, r4
	bl printf

.balign 4
begin_1:
	ldr r4, [fp, #-4]
	ldr r5, =1
	cmp r4, r5
	ble end_1
	ldr r4, [fp, #-4]
	ldr r5, [fp, #-8]
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	ldr r5, [fp, #-8]
	mul r4, r6, r5
	ldr r5, [fp, #-4]
	cmp r4, r5
	bne end_2
	ldr r0, =ifmt
	ldr r4, [fp, #-8]
	mov r1, r4
	bl printf
	ldr r0, =sfmt
	ldr r4, =str_4
	mov r1, r4
	bl printf
	ldr r4, [fp, #-4]
	ldr r5, [fp, #-8]
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	str r6, [fp, #-4]
	b begin_2

.balign 4
end_2:
	ldr r5, =1
	ldr r6, =5
	mul r4, r5, r6
	ldr r5, =8
	mov r0, r4
	mov r1, r5
	bl __aeabi_idiv
	mov r6, r0
	ldr r5, [fp, #-8]
	add r4, r5, r6
	ldr r6, =3
	add r5, r4, r6
	str r5, [fp, #-8]

.balign 4
begin_2:
	b begin_1

.balign 4
end_1:
	ldr r0, =sfmt
	ldr r4, =str_5
	mov r1, r4
	bl printf

.text
.balign 4
quit:
	mov sp, fp
	pop { lr }
	mov r0, #0
	bx lr

/********************
 * STRING CONSTANTS *
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
	.asciz "prime factors of "

.balign 4
str_4:
	.asciz ","

.balign 4
str_3:
	.asciz " are ["

.balign 4
str_5:
	.asciz "]\n"

