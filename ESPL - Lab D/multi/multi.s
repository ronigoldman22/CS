section .rodata
    mask: dw 0x002D
    format: db "%02hhx", 0
    newline: db 10, 0
section .data
    state: dw 0xACE1
    x_struct: db 5
        x_num: db 0xaa, 1,2,0x44,0x4f
    y_struct: db 5
        y_num: db 0xaa, 1,2,3,0x44
section .bss
    pointer: resd 1
    buffer: resb 600
section .text
global main
extern printf
extern fgets
extern malloc
extern strlen
extern stdin
extern free
main:
    push ebp
    mov ebp, esp

    mov eax, dword[ebp+8]       ;argc
    cmp eax, 1
    je no_arguments
    mov eax, dword[ebp+12]       ;argv
    mov eax, dword[eax+4]
    cmp word[eax], "-I"
    je from_stdin
    cmp word[eax], "-R"
    je random_numbers
    
    pop ebp
    ret
    

no_arguments:
    push y_struct
    push x_struct
    call add_multi
    add esp, 8

    push eax
    call print_multi
    call free
    add esp, 4

    pop ebp
    ret
from_stdin:
    call getmulti
    push eax
    call getmulti
    push eax
    call add_multi
    push eax
    call print_multi
    call free
    add esp, 4
    call free
    add esp, 4
    call free
    add esp, 4
    pop ebp
    ret
random_numbers:
    call PRmulti
    push eax
    call PRmulti
    push eax
    call add_multi
    push eax
    call print_multi
    call free
    add esp, 4
    call free
    add esp, 4
    call free
    add esp, 4
    pop ebp
    ret

print_multi:
    push ebp
    mov ebp, esp
    pushad

    mov esi, dword[ebp+8]       ;arg1 = x_struct
    mov edi, dword[esi]
    and edi, 0x000000FF         ;resets all bytes to 0 except the first byte than includes the size
print_loop:
    mov ebx, 0
    mov bl, byte[esi + edi]
    push ebx
    push format
    call printf
    add esp, 8
    dec edi
    cmp edi, 0
    jne print_loop
    push newline
    call printf
    add esp, 4
    popad
    pop ebp
    ret

getmulti:
    push ebp
    mov ebp, esp
    pushad
    ;fgets(buffer, 600, stdin);
    push dword[stdin]
    push 600
    push buffer
    call fgets
    add esp, 12

    push buffer
    call strlen
    add esp, 4
    mov edi, eax

    shr eax, 1          ;each 2 digits require 1 byte
    mov ebx, eax        ;how many bytes we need for the array
    add eax, 1          ;add 1 byte for size
    push eax
    call malloc
    add esp, 4
    mov dword[pointer], eax
    mov byte[eax], bl
    mov esi, buffer
    mov edx, 0
    sub edi, 2
getmulti_loop:
    ;12345 0x45 0x23 0x01
    mov ecx, 0
    mov bl, byte[esi + edi]
    dec edi
    cmp edi, 0
    jl skip_cl
    mov cl, byte[esi + edi]
    dec edi
    skip_cl:
    cmp bl, 'a'
    jl bl_number
    sub bl, 'a'
    add bl, 0xa
    jmp check_cl
    bl_number:
    sub bl, '0'
    check_cl:
    cmp cl, 'a'
    jl cl_number
    sub cl, 'a'
    add cl, 0xa
    jmp continue
    cl_number:
    sub cl, '0'
    continue:
    ;12345 bl = 0000 0101, cl = 0100 0000   0x45    0100 0101
    ;bl = 0000 0101
    ;cl = 0100 0000
    ;     0100 0101 - 0x45
    shl cl, 4
    or bl, cl
    mov byte[eax+edx+1], bl
    inc edx
    cmp edi, 0
    jge getmulti_loop

    popad
    mov eax, dword[pointer]
    pop ebp
    ret

add_multi:
    push ebp
    mov ebp, esp
    pushad
    mov eax, [ebp+8]
    mov ebx, [ebp+12]
    call Get_MaxMin
    push eax
    call print_multi
    push ebx
    call print_multi
    add esp, 8

    mov ecx, 0
    mov cl, byte[eax]
    add ecx, 2          ;size of eax + 1 in case of carry + 1 for size

    pushad
    push ecx
    call malloc
    add esp, 4
    mov dword[pointer], eax
    popad
    mov esi, dword[pointer]
    dec ecx             ;total bytes - 1 = size of num[]
    mov byte[esi], cl
    mov ecx, 0     
    mov edx, 0
    mov edi, 0
add_multi_loop:
    mov cl, ch          ;get the carray
    mov ch, 0           ;reset the carray
    add cl, byte[eax + edi + 1]     ;carray + eax->num[i]
    mov dl, byte[ebx + edi + 1]
    adc ecx, edx        ;if we have carray it will be in ch
    mov byte[esi + edi + 1], cl
    inc edi
    mov edx, edi
    cmp dl, byte[ebx]
    jne add_multi_loop
    cmp dl, byte[eax]
    je finished
add_bigger_number:
    mov edx, 0
    mov cl, ch          ;get the carray
    mov ch, 0           ;reset the carray
    mov dl, byte[eax + edi + 1]     ;carray + eax->num[i]
    add ecx, edx        ;if we have carray it will be in ch
    mov byte[esi + edi + 1], cl
    inc edi
    mov edx, edi
    cmp dl, byte[eax]
    jne add_bigger_number
    mov byte[esi + edi + 1], ch
    finished:
    popad
    pop ebp
    mov eax, dword[pointer]
    ret

Get_MaxMin:
    ;assumes that struct multi1 is in eax
    ;assumes that struct multi2 is in ebx
    ;returns the max(eax->size, ebx->size) in eax
    mov cl, byte[eax]
    cmp cl, byte[ebx]
    ja finish           ;checks unsigned numbers
    mov ecx, eax
    mov eax, ebx
    mov ebx, ecx
    finish:
    ret

rand_num:
    push ebp
    mov ebp, esp
    pushad
    mov ax, [state]
    and ax, [mask]
    mov ebx, 0
    parity:
        mov cx, ax
        and cx, 01
        xor bx, cx
        shr ax, 1
        jnz parity
    shr word[state], 1
    shl bx, 15
    or word[state], bx
    popad
    pop ebp
    mov eax, 0
    mov ax, [state]
    ret

PRmulti:
    push ebp
    mov ebp, esp
    pushad
loop_till_not_zero:
    call rand_num
    cmp al, 0
    je loop_till_not_zero

    mov ecx, 0
    mov cl, al
    inc al              ;size + 1 byte for the size variable
    mov ebx, 0
    mov bl, al
    pushad
    push ebx
    call malloc
    add esp, 4
    mov dword[pointer], eax
    popad
    mov esi, dword[pointer]
    mov byte[esi], cl
    mov edi, 0
print_loop_random:
    call rand_num
    mov byte[esi + edi + 1], al
    inc edi
    dec ecx
    cmp ecx, 0
    jne print_loop_random

    popad
    pop ebp
    mov eax, dword[pointer]
    ret
