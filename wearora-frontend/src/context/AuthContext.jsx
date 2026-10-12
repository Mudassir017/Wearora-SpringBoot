
import {
  createContext,
  useContext,
  useState,
} from "react";
import api from "../api/api";

const AuthContext = createContext(null);

function getSavedUser() {
  try {
    const savedUser = localStorage.getItem("wearora_user");
    return savedUser ? JSON.parse(savedUser) : null;
  } catch {
    localStorage.removeItem("wearora_user");
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(getSavedUser);

  async function login(email, password) {
    const response = await api.post("/auth/login", {
      email,
      password,
    });

    const data = response.data;

    if (!data?.token) {
      throw new Error("Login response did not contain a token.");
    }

    const loggedInUser = {
      id: data.userId,
      name: data.name,
      email: data.email,
    };

    localStorage.setItem("wearora_token", data.token);
    localStorage.setItem("wearora_user", JSON.stringify(loggedInUser));

    setUser(loggedInUser);

    return loggedInUser;
  }

  function logout() {
    localStorage.removeItem("wearora_token");
    localStorage.removeItem("wearora_user");
    setUser(null);
  }

  const value = {
    user,
    login,
    logout,
    isAuthenticated: Boolean(user && localStorage.getItem("wearora_token")),
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}
