import { useState, useEffect } from "react";
import { fetchHackathons } from "../api/hackathonService";
import type { Hackathon } from "../api/hackathonService";

export function useHackathons() {
    const isAuthenticated = Boolean(localStorage.getItem('token'));
    const [hackathons, setHackathons] = useState<Hackathon[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let isMounted = true;

        const loadData = async () => {
            try {
                const data = await fetchHackathons();
                if (isMounted) setHackathons(data);
            } catch (err: unknown) {
                if (isMounted) {
                    if (err instanceof Error) setError(err.message);
                    else setError('Si è verificato un errore anomalo');
                }
            } finally {
                if (isMounted) setLoading(false);
            }
        };

        void loadData();

        return () => { isMounted = false; };
    }, []);

    return { hackathons, loading, error, isAuthenticated };
}