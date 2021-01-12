package controllers

type ApiErrorCode int

const (
	Success ApiErrorCode = 0

	EndPointFailedLogIn   ApiErrorCode = 1001
	EndPointFailedLogOut  ApiErrorCode = 1002
	EndPointFailedRefresh ApiErrorCode = 1003
)
