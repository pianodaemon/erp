import jwt_decode from 'jwt-decode';
import Cookies from 'universal-cookie';


let cookies = new Cookies();

export class TokenStorage {

    private static readonly COOKIE_STORAGE_TOKEN = 'token';

    public static isAuthenticated(): boolean {

        let validToken: boolean = false;

        try {

            validToken = Boolean(jwt_decode(this.getToken() || ''));
        } catch(e) {

            // It is requiered to print this event into the log
        }

        return validToken;
    }

    public static getToken(): string | null {

        return cookies.get(TokenStorage.COOKIE_STORAGE_TOKEN) || null;
    }
}
