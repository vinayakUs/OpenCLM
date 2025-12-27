export interface ApiError {
    message: string;
    code: string;
}

export interface ApiResponse<T> {
    success: boolean;
    error: ApiError;
    data: T;
}
