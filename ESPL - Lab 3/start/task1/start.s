section .data
newline_format: db 10
myCh: db 0
input_file: dd 0
output_file: dd 1
section .text
global main
extern strlen
main:    
  ;  FILE* input_file = stdin;
   ; FILE* output_file = stdout;
    push ebp ; push base pointer into stack
    mov ebp, esp ; move the stack pointer (esp) into ebp
    mov eax, dword[ebp + 8] ; argc
    mov ebx, dword[ebp + 12] ; argv
    add ebx, 4 ; skip over the first element (program name)
    dec eax
loop_start:
    cmp eax, 0
    je encoder ; if not end loop again
    pushad
    push dword[ebx]
    call strlen
    add esp, 4
    mov edx, eax
    mov eax, 4
    mov ecx, dword[ebx]
    mov ebx, 1
    int 0x80
    
    mov eax, 4
    mov ebx, 1
    mov ecx, newline_format
    mov edx, 1
    int 0x80
    popad
    pushad
    mov ebx, dword[ebx]
    cmp word[ebx], "-i"
    je open_input
    cmp word[ebx], "-o"
    je open_output
    continue_loop:
    popad
    dec eax
    add ebx, 4
    jmp loop_start
encoder:
    mov eax, 3
    mov ebx, dword[input_file]
    mov ecx, myCh
    mov edx, 1
    int 0x80
    cmp eax, 1
    jne exit
    cmp byte[myCh], 'A'
    jl print_char
    cmp byte[myCh], 'z'
    jg print_char
    inc byte[myCh]

print_char:
    mov eax, 4
    mov ebx, dword[output_file]
    mov ecx, myCh
    mov edx, 1
    int 0x80
    jmp encoder
exit:
    pop ebp
    ret

open_input:
    mov eax, 5
    add ebx, 2
    mov ecx, 0
    int 0x80
    cmp eax, 0
    jl error
    mov dword[input_file], eax
    jmp continue_loop
open_output:
    mov eax, 5
    add ebx, 2
    mov ecx, 0x41
    mov edx, 644o
    int 0x80
    cmp eax, 0
    jl error
    mov dword[output_file], eax
    jmp continue_loop
error:
    popad
    jmp exit